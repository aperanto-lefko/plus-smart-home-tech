package ru.yandex.practicum.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Slf4j
public class AnalyzerExecutorConfig {
    @Bean(name = "snapshotAnalyzer")
    public ExecutorService snapshotAnalyzerExecutor() {
        return Executors.newSingleThreadExecutor(r -> {
                    log.info("Создание потока event-snapshot-consumer");
                    return new Thread(r, "snapshot - analyzer");
                }
        );
    }
    @Bean(name = "hubEventAnalyzer")
    public ExecutorService hubEventAnalyzerExecutor() {
        return Executors.newSingleThreadExecutor(r -> {
                    log.info("Создание потока hubEvent - analyzer");
                    return new Thread(r, "hubEvent - analyzer");
                }
        );
    }
}
