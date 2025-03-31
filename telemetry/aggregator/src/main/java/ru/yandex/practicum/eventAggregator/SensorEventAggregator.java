package ru.yandex.practicum.eventAggregator;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.deserializer.DeserializerType;
import ru.yandex.practicum.handler.SnapshotHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SensorEventAggregator extends KafkaAggregator<String, SensorEventAvro, SensorsSnapshotAvro> {
    final String inputTopic;
    final String outPutTopic;
    final SensorSnapshotUpdater updater;
    final Function<DeserializerType, KafkaConsumer<String, SensorEventAvro>> consumerFactory;


    @Autowired
    public SensorEventAggregator(SnapshotHandler<SensorsSnapshotAvro> snapshotHandler,
                                 SensorSnapshotUpdater updater,
                                 Function<DeserializerType, KafkaConsumer<String, SensorEventAvro>> consumerFactory,
                                 @Value("${kafka.topics.sensor_events_topic}") String inputTopic,
                                 @Value("${kafka.topics.snapshots_topic}") String outPutTopic) {
        super(snapshotHandler);
        this.updater = updater;
        this.consumerFactory = consumerFactory;
        this.consumer = consumerFactory.apply(DeserializerType.SENSOR_EVENT_DESERIALIZER);
        this.inputTopic = inputTopic;
        this.outPutTopic = outPutTopic;
        log.info("Consumer инициализирован для топиков: {}", getInputTopics());
    }

    /*@PostConstruct
    public void init() {
        KafkaConsumer<String, SensorEventAvro> consumer =
                consumerFactory.apply(DeserializerType.SENSOR_EVENT_DESERIALIZER);
        this.consumer = consumer;

        log.info("Consumer инициализирован для топиков: {}", getInputTopics());
    }*/


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
        return updater.updateState(record);
    }

}
