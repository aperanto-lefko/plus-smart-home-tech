package ru.yandex.practicum.consumer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Slf4j
public abstract class KafkaEventReceiver<K, V> {
    protected final KafkaConsumer<K, V> consumer;
    private volatile boolean running = true;
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private static final Duration POLL_TIMEOUT = Duration.ofMillis(100);

    protected abstract List<String> getTopics();  // Топики, на которые подписываемся

    @PostConstruct
    public void start() {
        log.info("Подписка на топики: {}", getTopics());
        consumer.subscribe(getTopics());
        new Thread(this::pollLoop).start();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Завершение работы агрегатора");
        running = false;
        waitForCompletion();
        consumer.wakeup();
    }

    private void pollLoop() {
        try {
            while (running) {
                ConsumerRecords<K, V> records = consumer.poll(POLL_TIMEOUT);
                if (!records.isEmpty()) {
                    processing.set(true);
                    processRecords(records);
                    commitOffsets();
                    processing.set(false);
                }
            }
        } catch (WakeupException ignored) {
            // Игнорируем при shutdown
        } catch (Exception e) {
            log.error("Ошибка в pollLoop:", e);
        } finally {
            closeResources();
        }
    }

    private void processRecords(ConsumerRecords<K, V> records) {
        for (ConsumerRecord<K, V> record : records) {
            try {
                EventReceiver<V> handler = getHandler();
                handler.handle(record.value());
            } catch (Exception e) {
                log.error("Ошибка обработки записи [{}:{}]", record.topic(), record.offset(), e);
            }
        }
    }

    protected abstract EventReceiver<V> getHandler();  // Возвращает обработчик сообщений

    private void commitOffsets() {
        try {
            consumer.commitSync();
            log.debug("Офсеты успешно зафиксированы");
        } catch (Exception e) {
            log.error("Ошибка коммита офсетов", e);
        }
    }

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
            log.info("Закрытие consumer...");
            consumer.close();
        } catch (Exception e) {
            log.error("Ошибка при закрытии consumer", e);
        }
    }
}
