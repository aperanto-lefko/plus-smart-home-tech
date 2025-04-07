package ru.yandex.practicum.receiver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerManager<K, V> {
    @Value("${kafka.consumer_manager.shutdown_timeout}")
    private int shutdownTimeout;
    @Value("${kafka.consumer_manager.poll_timeout}")
    private int pollTimeout;
    private final KafkaConsumer<K, V> consumer;
    private final ExecutorService executorService;
    private volatile boolean running = true;

    public void subscribe(List<String> topics) {
        log.info("Подписка на топики: {}", topics);
        consumer.subscribe(topics);
    }

    public void startPolling(Consumer<ConsumerRecords<K, V>> handler) {
        executorService.submit(() -> {
            String threadName = Thread.currentThread().getName();
            try {

                while (running) {
                    try {
                        //log.info("Поток {} выполняет poll()", threadName);
                        ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(pollTimeout));
                        if (!records.isEmpty()) {
                            log.info("Поток {} получил {} сообщений", threadName, records.count());
                            handler.accept(records);
                        }
                    } catch (WakeupException e) {
                        if (!running) {
                            log.info("Поток {} получил WakeupException при завершении работы", threadName);
                            break;
                        }
                        log.warn("Поток {} получил неожиданный WakeupException", threadName, e);
                    }
                }
            } catch (Exception e) {
                log.error("Поток {} завершился с ошибкой", threadName, e);

            } finally {
                log.info("Поток {} завершает работу", threadName);
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
