package ru.yandex.practicum.aggregator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.receiver.BaseAggregator;
import ru.yandex.practicum.receiver.KafkaConsumerManager;
import ru.yandex.practicum.receiver.OffsetCommitManager;
import ru.yandex.practicum.record_process.SnapshotButchProcessor;

import java.util.List;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SnapshotAggregator extends BaseAggregator<String, SensorsSnapshotAvro> {

    @Autowired
    public SnapshotAggregator(KafkaConsumerManager<String, SensorsSnapshotAvro> consumerManager,
                              OffsetCommitManager<String, SensorsSnapshotAvro> offsetCommitManager) {
        super (consumerManager, offsetCommitManager);
    }
    @Value("${kafka.topics.snapshots_topic}")
    private String inputTopic;

    @Override
    protected List<String> getInputTopics() {
        return List.of(inputTopic);
    }
    @Override
protected SnapshotButchProcessor<String, SensorsSnapshotAvro> createBatchProcessor()
    {
       return new SnapshotButchProcessor<>(
               offsetCommitManager,
               processing
       );
    }
}
