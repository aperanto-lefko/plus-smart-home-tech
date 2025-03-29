package ru.yandex.practicum.deserializer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.context.annotation.Bean;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@RequiredArgsConstructor
public class SensorEventDeserializer {
//удалить
    private final DeserializeFactory factory;

    @Bean
    public Deserializer<SensorEventAvro> sensorEventDeserializer() {
        return factory.createDeserializer(DeserializerType.SENSOR_EVENT_DESERIALIZER);
    }
}
