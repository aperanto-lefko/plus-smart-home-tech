package ru.yandex.practicum.deserializer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;

@Component
public class DeserializeFactory {
    public <T extends SpecificRecordBase> Deserializer<T> createDeserializer(Class<T> targetType) {
        return new BaseAvroDeserializer<>(targetType);
    }
}
