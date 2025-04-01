package ru.yandex.practicum.eventAggregator;


import lombok.AccessLevel;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaConsumerCreator;
import ru.yandex.practicum.deserializer.DeserializerType;
import ru.yandex.practicum.handler.SnapshotHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;
import java.util.Optional;


@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SensorEventAggregator extends KafkaAggregator<String, SensorEventAvro, SensorsSnapshotAvro> {
    final String inputTopic;
    final String outPutTopic;
    final SensorSnapshotUpdater updater;
    final KafkaConsumerCreator creator;

    @Autowired
    public SensorEventAggregator(SnapshotHandler<SensorsSnapshotAvro> snapshotHandler,
                                 SensorSnapshotUpdater updater,
                                 KafkaConsumerCreator creator,
                                 @Value("${kafka.topics.sensor_events_topic}") String inputTopic,
                                 @Value("${kafka.topics.snapshots_topic}") String outPutTopic) {
        super(snapshotHandler);
        this.updater = updater;
        this.creator = creator;
        this.consumer = creator.createConsumer(DeserializerType.SENSOR_EVENT_DESERIALIZER,
                SensorEventAvro.class);
        this.inputTopic = inputTopic;
        this.outPutTopic = outPutTopic;
        log.info("Consumer инициализирован для топиков: {}", getInputTopics());
    }

    @Override
    protected List<String> getInputTopics() {
        return List.of(inputTopic);
    }

    @Override
    protected String getOutputTopic() {
        return outPutTopic;
    }

    @Override
    protected Optional<SensorsSnapshotAvro> processRecord(SensorEventAvro record) {
        log.info("Производится верификация сообщения...");
        return updater.updateState(record);
    }

}
