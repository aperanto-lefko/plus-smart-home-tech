package ru.yandex.practicum.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka.consumer")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Slf4j
public class KafKaConsumerConfig {
}
