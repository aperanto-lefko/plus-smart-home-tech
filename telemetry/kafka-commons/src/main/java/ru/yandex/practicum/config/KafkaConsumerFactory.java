package ru.yandex.practicum.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.deserializer.DeserializeFactory;
import ru.yandex.practicum.deserializer.DeserializerType;

import java.util.Properties;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class KafkaConsumerFactory {
    final KafKaConsumerProperties config;
    final DeserializeFactory deserializeFactory;

    public <V extends SpecificRecordBase> KafkaConsumer<String, V> createConsumer(
            DeserializerType deserializerType,
            Class<V> valueType) {
        log.info("Бин фабрики успешно создан");

        Properties properties = config.buildProperties();
        Deserializer<V> deserializer = deserializeFactory.createDeserializer(deserializerType);
        log.info("Загруженная конфигурация: " +
                properties + "key_deserialize_class: StringDeserializer, value_deserialize_class:" +
                deserializer.getClass().getName());
        KafkaConsumer<String, V> consumer = new KafkaConsumer<>(properties,
                new StringDeserializer(),
                deserializer);
        log.info("Успешно создан kafka-consumer");
        return consumer;
    }
}
