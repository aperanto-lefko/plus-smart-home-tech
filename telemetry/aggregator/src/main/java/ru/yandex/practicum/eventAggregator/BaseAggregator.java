package ru.yandex.practicum.eventAggregator;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.SnapshotHandler;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@Component
@RequiredArgsConstructor
public abstract class BaseAggregator<K, V, R> {
    protected KafkaConsumer<K, V> consumer;
    private final SnapshotHandler<R> snapshotHandler;
    private static final Duration CONSUME_TIMEOUT = Duration.ofMillis(100);
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 60;
    private static final int BATCH_SIZE = 10; // размер батча для коммита
    private volatile boolean running = true; //флаг для управления основным циклом обработки
    private final AtomicBoolean processing = new AtomicBoolean(false); //Чтобы понимать, идет ли сейчас обработка сообщений
    protected abstract List<String> getInputTopics(); //возвращает топики для подписки
    protected abstract Optional<R> processRecord(V record); //обработка одного сообщения
    private final ConcurrentHashMap<TopicPartition, OffsetAndMetadata> offsetsToCommit = new ConcurrentHashMap<>();
    private final AtomicInteger processedInBatch = new AtomicInteger(0);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void start() {
        log.info("Подписка на топики: {}", getInputTopics());
        consumer.subscribe(getInputTopics());
        executorService.submit(this::pollLoop);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Начало завершения работы агрегатора...");
        running = false; //флаг остановки для pollloop
        // Остановка ExecutorService
        executorService.shutdown();// Запрещает новые задачи
        try {// Ожидаем завершения текущих задач:
            if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                log.info("Принудительное завершение после таймаута");
                executorService.shutdownNow();
                waitForCompletion();
            }
        } catch (InterruptedException e) {
            log.error("Работа прервана interrupt", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        // Фиксация оставшихся офсетов
        log.info("Фиксация оставшихся офсетов");
        commitOffsets(offsetsToCommit);
        // Завершение работы consumer
        consumer.wakeup();
        log.info("Агрегатор успешно остановлен");
    }

    private void pollLoop() {
        try {
            while (running) {
                ConsumerRecords<K, V> records = consumer.poll(CONSUME_TIMEOUT);
                if (!records.isEmpty()) {
                    processing.set(true);
                    processRecords(records);
                    processing.set(false);
                }
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем consumer и продюсер в блоке finally
            log.info("Закрытие consumer...");
        } catch (Exception e) {
            log.error("Ошибка обработки", e);
        } finally {
            closeResources();
        }
    }

    private void processRecords(ConsumerRecords<K, V> records) {
        for (ConsumerRecord<K, V> record : records) {
            try {
                Optional<R> result = processRecord(record.value());
                result.ifPresent(snapshotHandler::handle);
                log.info("Получено сообщение для обработки {}", result);
                offsetsToCommit.put(
                        new TopicPartition(record.topic(), record.partition()), //добавить счетчик комитить по 10
                        new OffsetAndMetadata(record.offset() + 1));
                if (processedInBatch.incrementAndGet() % BATCH_SIZE == 0) {
                    log.info("Фиксация партии из {} офсетов", BATCH_SIZE);
                    commitOffsets(offsetsToCommit);
                    processedInBatch.set(0);
                }
            } catch (Exception e) {
                log.error("Ошибка обработки записи [{}:{}]", record.topic(), record.offset(), e);
            }
        }
    }

    private void commitOffsets(Map<TopicPartition, OffsetAndMetadata> offsetsToCommit) {
        try {
            if (!offsetsToCommit.isEmpty()) {
                consumer.commitSync(offsetsToCommit);
                log.info("Офсеты успешно зафиксированы для {} партиций", offsetsToCommit.size());
                offsetsToCommit.clear();
            }
        } catch (CommitFailedException e) {
            log.error("Ошибка фиксации офсетов для {} партиций", offsetsToCommit.size(), e);
        } catch (WakeupException e) {
            log.warn("Коммит прерван WakeupException");
            throw e; // Пробрасываем для обработки в pollLoop
        }
    }

    /*
     * Ожидает завершения текущей обработки при shutdown
     */
    private void waitForCompletion() {
        int retries = 0;
        while (processing.get() && retries++ < 5) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void closeResources() {
        try {
            log.info("Завершающая фиксация офсетов...");
            commitOffsets(offsetsToCommit);
            log.info("Закрытие консьюмера...");
            consumer.close(Duration.ofSeconds(5));
        } catch (Exception e) {
            log.error("Ошибка при освобождении ресурсов", e);
        }
    }
}
