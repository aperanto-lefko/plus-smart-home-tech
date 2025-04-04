package ru.yandex.practicum.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Slf4j
public class AggregatorExecutorConfig {
    @Bean(name = "snapshotExecutor")
    public ExecutorService snapshotExecutor() {
        return Executors.newSingleThreadExecutor(r -> {
            log.info("Создание потока event-snapshot-consumer");
               return new Thread(r, "event-snapshot-consumer");}
        );
    }
}
