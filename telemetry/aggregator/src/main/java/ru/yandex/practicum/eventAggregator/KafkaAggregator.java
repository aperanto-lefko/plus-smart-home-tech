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
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
@Component
@RequiredArgsConstructor
public abstract class KafkaAggregator<K, V, R> {
    protected KafkaConsumer<K, V> consumer;
    private final SnapshotHandler<R> snapshotHandler;
    private static final Duration CONSUME_TIMEOUT = Duration.ofMillis(100);
    private volatile boolean running = true; //флаг для управления основным циклом обработки
    private final AtomicBoolean processing = new AtomicBoolean(false); //Чтобы понимать, идет ли сейчас обработка сообщений

    protected abstract List<String> getInputTopics(); //возвращает топики для подписки

    protected abstract String getOutputTopic(); //выходной топик

    protected abstract Optional<R> processRecord(V record); //обработка одного сообщения
    Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();
    int batchSize = 10; // размер батча для коммита
    int processedInBatch = 0;

    @PostConstruct //запускается при инициализации бина Spring
    public void start() {
        log.info("Подписка на топики: {}", getInputTopics());
        consumer.subscribe(getInputTopics());
        new Thread(this::pollLoop).start();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Фиксация оставшихся офсетов перед окончанием работы");
        if(!offsetsToCommit.isEmpty()) {
            commitOffsets(offsetsToCommit);
        }
        log.info("Завершение работы агрегатора");
        running = false;
        waitForCompletion();
        consumer.wakeup();
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
                processedInBatch++;
                if (processedInBatch % batchSize == 0) {
                    log.info("Фиксация партии из {} офсетов", batchSize);
                    processedInBatch=0;
                    commitOffsets(offsetsToCommit);

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
               offsetsToCommit.clear(); // Очищаем мапу после коммита
           }
       } catch (CommitFailedException e) {
           log.error("Ошибка фиксации офсетов для {} партиций", offsetsToCommit.size(), e);
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
            consumer.commitSync();
            log.info("Закрытие консьюмера...");
            consumer.close(Duration.ofSeconds(5));
        } catch (Exception e) {
            log.error("Ошибка при освобождении ресурсов", e);
        }
    }
}
