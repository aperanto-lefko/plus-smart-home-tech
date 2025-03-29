package ru.yandex.practicum.handler;


import lombok.extern.slf4j.Slf4j;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.producer.KafkaEventSender;

@Slf4j
public class SensorsSnapshotHandler extends KafkaEventSender<SensorsSnapshotAvro> implements SnapshotHandler {
    @Value("${kafka.topics.snapshots_topic}")
    private String snapshotsTopic;

    @Autowired
    public SensorsSnapshotHandler(KafkaProducer<String, SpecificRecordBase> kafkaProducer) {
        super(kafkaProducer);
    }

    @Override
    public void handle(SensorsSnapshotAvro event) {
        sendToKafka(event, snapshotsTopic);
    }
}
