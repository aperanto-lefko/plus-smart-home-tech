package ru.yandex.practicum.configuration;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.deserializer.DeserializerType;
import ru.yandex.practicum.eventAggregator.SensorEventAggregator;
import ru.yandex.practicum.eventAggregator.SensorSnapshotUpdater;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.function.Function;

@Configuration
public class AggregatorConfiguration {
    @Bean
    public SensorEventAggregator sensorEventAggregator (
            SensorSnapshotUpdater updater,
            Function<DeserializerType, KafkaConsumer<String, SpecificRecordBase>> consumerFactory,
            KafkaProducer<String, SensorsSnapshotAvro> producer) {
        return new SensorEventAggregator(updater, consumerFactory, producer);
    }
}
