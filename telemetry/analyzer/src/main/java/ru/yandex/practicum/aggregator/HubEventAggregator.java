package ru.yandex.practicum.aggregator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.receiver.BaseAggregator;
import ru.yandex.practicum.receiver.KafkaConsumerManager;
import ru.yandex.practicum.receiver.OffsetCommitManager;
import ru.yandex.practicum.record_process.EventButchProcessor;
import ru.yandex.practicum.record_process.RecordProcessor;


import java.util.List;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HubEventAggregator extends BaseAggregator<String, HubEventAvro> {
    final RecordProcessor<HubEventAvro> recordProcessor;

    @Autowired
    public HubEventAggregator(KafkaConsumerManager<String, HubEventAvro> consumerManager,
                              OffsetCommitManager<String, HubEventAvro> offsetCommitManager,
                              RecordProcessor<HubEventAvro> recordProcessor) {
        super(consumerManager, offsetCommitManager);
        this.recordProcessor = recordProcessor;
    }

    @Value("${kafka.topics.hub_events_topic}")
    private String inputTopic;

    @Override
    protected List<String> getInputTopics() {
        return List.of(inputTopic);
    }

    @Override
    protected EventButchProcessor<String, HubEventAvro> createBatchProcessor() {
        return new EventButchProcessor<>(
                offsetCommitManager,
                processing,
                recordProcessor
        );
    }
}
