package ru.yandex.practicum.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({KafkaProducerConfig.class, KafKaConsumerConfig.class})
public class AppConfig {
}
