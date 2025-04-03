package ru.yandex.practicum.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.receiver.KafkaConsumerManager;
import ru.yandex.practicum.receiver.OffsetCommitManager;
import ru.yandex.practicum.record_process.RecordProcessor;
import ru.yandex.practicum.record_process.RecordsBatchProcessor;
import ru.yandex.practicum.deserializer.DeserializerType;
import ru.yandex.practicum.handler.SnapshotHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
@RequiredArgsConstructor
public class AggregatorConfig {
    private final KafkaConsumerFactory factory;
    private final SnapshotHandler<SensorsSnapshotAvro> snapshotHandler;
    private final RecordProcessor<SensorEventAvro, SensorsSnapshotAvro> sensorEventProcessor;

    @Bean
    public KafkaConsumer<String, SensorEventAvro> sensorEventConsumer() {
        return factory.createConsumer(DeserializerType.SENSOR_EVENT_DESERIALIZER, SensorEventAvro.class);
    }
    @Bean
    public OffsetCommitManager<String, SensorEventAvro> offsetCommitManager(
            KafkaConsumer<String, SensorEventAvro> consumer) {
        return new OffsetCommitManager<>(consumer);
    }
    @Bean
    public KafkaConsumerManager<String, SensorEventAvro> sensorEventConsumerManager(
            KafkaConsumer<String, SensorEventAvro> consumer) {
        return new KafkaConsumerManager<>(consumer);
    }

}
