package ru.yandex.practicum.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.deserializer.DeserializerType;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.receiver.KafkaConsumerManager;
import ru.yandex.practicum.receiver.OffsetCommitManager;

import java.util.concurrent.ExecutorService;

@Configuration
@RequiredArgsConstructor
public class AnalyzerConfig {
    private final KafkaConsumerFactory factory;

    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer() {
        return factory.createConsumer(DeserializerType.SENSOR_SNAPSHOT_DESERIALIZER, SensorsSnapshotAvro.class);
    }

    @Bean
    public KafkaConsumer<String, HubEventAvro> nubEventConsumer() {
        return factory.createConsumer(DeserializerType.HUB_EVENT_DESERIALIZER, HubEventAvro.class);
    }

    @Bean
    public OffsetCommitManager<String, SensorsSnapshotAvro> offsetCommitSnapshotManager(
            KafkaConsumer<String, SensorsSnapshotAvro> consumer) {
        return new OffsetCommitManager<>(consumer);
    }

    @Bean
    public OffsetCommitManager<String, HubEventAvro> offsetCommitHubEventManager(
            KafkaConsumer<String, HubEventAvro> consumer) {
        return new OffsetCommitManager<>(consumer);
    }

    @Bean
    public KafkaConsumerManager<String, SensorsSnapshotAvro> snapshotConsumerManager(
            KafkaConsumer<String, SensorsSnapshotAvro> consumer,
            @Qualifier("snapshotAnalyzer") ExecutorService executor) {
        return new KafkaConsumerManager<>(consumer, executor);
    }

    @Bean
    public KafkaConsumerManager<String, HubEventAvro> hubEventConsumerManager(
            KafkaConsumer<String, HubEventAvro> consumer,
            @Qualifier("hubEventAnalyzer") ExecutorService executor) {
        return new KafkaConsumerManager<>(consumer, executor);
    }
}
