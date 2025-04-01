package ru.yandex.practicum.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.Properties;
@Configuration
@ConfigurationProperties(prefix = "kafka.producer")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerConfig {

//    String bootstrapServer;
//    String keySerializeClass;
//    String valueSerializeClass;
    KafkaProducer<String, SpecificRecordBase> pr;
    final KafkaProducerProperties config;

//    @PostConstruct
//    public void logConfig() {
//        log.info("Загруженная конфигурация: bootstrap={}, keySerializer={}, valueSerializer={}",
//                bootstrapServer, keySerializeClass, valueSerializeClass);
//    }
   @Bean
    public KafkaProducer<String, SpecificRecordBase> producer() {
        Properties properties = config.buildProperties();
       log.info("Загруженная конфигурация {}: ", properties);
//        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
//        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializeClass);
//        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializeClass);
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
