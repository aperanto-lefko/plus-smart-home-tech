package ru.yandex.practicum.config;

import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.Properties;
@Configuration

@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "kafka-commons.kafka.producer")
@Getter
@Setter
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerFactory {
    KafkaProducer<String, SpecificRecordBase> pr;
    final KafkaProducerProperties config;

   @Bean
 public KafkaProducer<String, SpecificRecordBase> producer() {
        Properties properties = config.buildProperties();
        if (properties==null) {
            log.info("Настройки не загружены");
        }
       log.info("Загруженная конфигурация {}: ", properties);
        pr = new KafkaProducer<>(properties);
        log.info("Создан kafka-producer {}", pr);
        return pr;
    }

    @PreDestroy
    public void closeProducer() {
        if (pr != null) {
            pr.close();
            log.info("kafka-producer закрыт");
        }
    }
}
