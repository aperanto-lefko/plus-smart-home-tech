package ru.yandex.practicum.eventAggregator;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.deserializer.DeserializerType;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.producer.KafkaEventSender;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
//@RequiredArgsConstructor
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SensorEventAggregator extends KafkaAgregator<String, SensorEventAvro, SensorsSnapshotAvro> {
    @Value("${kafka.topics.sensor_events_topic}")
    String inputTopic;
    @Value("${kafka.topics.snapshots_topic}")
    String outPutTopic;
    final SensorSnapshotUpdater updater;
    final Function<DeserializerType, KafkaConsumer<String, SpecificRecordBase>> consumerFactory;


    @Autowired
    public SensorEventAggregator(SensorSnapshotUpdater updater,
                                 Function<DeserializerType, KafkaConsumer<String, SpecificRecordBase>> consumerFactory,
                                 KafkaProducer<String, SensorsSnapshotAvro> producer) {
        super(producer);
        this.updater = updater;
        this.consumerFactory = consumerFactory;

    }

    @PostConstruct
    public void init() {
        KafkaConsumer<String, SpecificRecordBase> consumer =
                consumerFactory.apply(DeserializerType.SENSOR_EVENT_DESERIALIZER);
        this.consumer = consumer;

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
        return updater.updateState(record);
    }
    /*
      @Override
    protected String getOutputKey(SensorsSnapshotAvro result) {
        return result.getHubId();
    }
     */
}
