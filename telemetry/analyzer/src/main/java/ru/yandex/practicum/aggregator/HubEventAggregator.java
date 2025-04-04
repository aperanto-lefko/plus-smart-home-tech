package ru.yandex.practicum.aggregator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.receiver.BaseAggregator;
import ru.yandex.practicum.receiver.KafkaConsumerManager;
import ru.yandex.practicum.receiver.OffsetCommitManager;
import ru.yandex.practicum.record_process.HubEventButchProcessor;
import ru.yandex.practicum.record_process.SnapshotButchProcessor;

import java.util.List;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HubEventAggregator extends BaseAggregator<String, HubEventAvro> {
    @Autowired
    public HubEventAggregator(KafkaConsumerManager<String, HubEventAvro> consumerManager,
                              OffsetCommitManager<String, HubEventAvro> offsetCommitManager) {
        super (consumerManager, offsetCommitManager);
    }
    @Value("${kafka.topics.hub_events_topic}")
    private String inputTopic;

    @Override
    protected List<String> getInputTopics() {
        return List.of(inputTopic);
    }

    @Override
    protected HubEventButchProcessor<String, HubEventAvro> createBatchProcessor()
    {
        return new HubEventButchProcessor<>(
                offsetCommitManager,
                processing
        );
    }
}
