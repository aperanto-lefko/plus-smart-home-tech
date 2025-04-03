package ru.yandex.practicum.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumerManager <K, V> {
    @Value("${kafka.consumer_manager.shutdown_timeout}")
    private int shutdownTimeout;
    @Value("${kafka.consumer_manager.poll_timeout}")
    private int pollTimeout;
    private final KafkaConsumer<K, V> consumer;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private volatile boolean running = true;

    public void subscribe(List<String> topics) {
        log.info("Подписка на топики: {}", topics);
        consumer.subscribe(topics);
    }

    public void startPolling(Consumer<ConsumerRecords<K,V>> handler) {
        executorService.submit(() -> {
            try {
                while (running) {
                    ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(pollTimeout));
                    if (!records.isEmpty()) {
                        handler.accept(records);
                    }
                }
            } catch (Exception e) {
                log.error("Загрузка сообщений pool завершилась с ошибкой", e);
            } finally {
                closeResources();
            }
        });
    }

    public void shutdown() {
        running = false;
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(shutdownTimeout, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        consumer.wakeup();
    }

    private void closeResources() {
        try {
            consumer.close(Duration.ofSeconds(5));
        } catch (Exception e) {
            log.error("Ошибка при закрытии consumer", e);
        }
    }
}
