package ru.yandex.practicum.deserializer;

import jakarta.annotation.PostConstruct;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.NullValueException;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;

@Component
public class DeserializeFactory {

    private final Map<DeserializerType, Class<? extends SpecificRecordBase>> registry = new HashMap<>();

    public DeserializeFactory() {
        this.registry.put(DeserializerType.SENSOR_EVENT_DESERIALIZER, SensorEventAvro.class);
        this.registry.put(DeserializerType.HUB_EVENT_DESERIALIZER, HubEventAvro.class);
        this.registry.put(DeserializerType.SENSOR_SNAPSHOT_DESERIALIZER, SensorsSnapshotAvro.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends SpecificRecordBase> Deserializer<T> createDeserializer(DeserializerType type) {
        Class<? extends SpecificRecordBase> targetType = registry.get(type);
        if (targetType == null) {
            throw new NullValueException("Неизвестный тип десериализатора " + type);
        }
        // Безопасное приведение, контролируем типы в registry
        return (Deserializer<T>) new BaseAvroDeserializer<>(targetType);
    }
}
