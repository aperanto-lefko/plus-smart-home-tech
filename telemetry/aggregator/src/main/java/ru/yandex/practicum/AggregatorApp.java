package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.config.KafkaProducerFactory;
import ru.yandex.practicum.config.KafkaProducerProperties;


@SpringBootApplication(scanBasePackages = "ru.yandex.practicum")
public class AggregatorApp {
    public static void main(String[] args) {
        SpringApplication.run(AggregatorApp.class, args);
    }
}