package ru.yandex.practicum.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.aggregator.KafkaConsumerManager;
import ru.yandex.practicum.aggregator.OffsetCommitManager;
import ru.yandex.practicum.aggregator.RecordProcessor;
import ru.yandex.practicum.aggregator.RecordsBatchProcessor;
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


    @Bean
    public RecordsBatchProcessor<String, SensorEventAvro, SensorsSnapshotAvro> recordsBatchProcessor(
            OffsetCommitManager<String, SensorEventAvro> offsetCommitManager) {
        return new RecordsBatchProcessor<>(
                sensorEventProcessor,
                snapshotHandler,
                offsetCommitManager,
                new AtomicBoolean(false)
        );
    }
}
