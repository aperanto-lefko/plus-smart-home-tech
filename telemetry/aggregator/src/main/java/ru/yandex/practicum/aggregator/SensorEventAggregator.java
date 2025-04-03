package ru.yandex.practicum.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaConsumerFactory;
import ru.yandex.practicum.handler.SnapshotHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Component
public class SensorEventAggregator extends BaseAggregator<String, SensorEventAvro, SensorsSnapshotAvro> {


    @Autowired
    public SensorEventAggregator(KafkaConsumerManager<String, SensorEventAvro> consumerManager,
                                 RecordProcessor<SensorEventAvro, SensorsSnapshotAvro> recordProcessor,
                                 OffsetCommitManager<String, SensorEventAvro> offsetCommitManager,
                                 SnapshotHandler<SensorsSnapshotAvro> snapshotHandler) {
        super(consumerManager, recordProcessor, offsetCommitManager, snapshotHandler);
    }

    @Value("${kafka.topics.sensor_events_topic}")
    private String inputTopic;

    @Override
    protected List<String> getInputTopics() {
        return List.of(inputTopic);
    }

    @Override
    protected RecordsBatchProcessor<String, SensorEventAvro, SensorsSnapshotAvro> createBatchProcessor() {
        return new RecordsBatchProcessor<>(
                recordProcessor,
                snapshotHandler,
                offsetCommitManager,
                processing
        );
    }
}
