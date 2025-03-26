package ru.yandex.practicum.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.mapper.SensorEventMapper;

@Slf4j
public abstract class BaseSensorEventHandler extends BaseEventHandler<SensorEventProto>
        implements SensorEventHandler {
    @Value("${kafka.topics.sensor_events_topic}")
    protected String sensorEventsTopic;
    protected final SensorEventMapper sensorEventMapper;

    protected BaseSensorEventHandler(KafkaProducer<String, SpecificRecordBase> kafkaProducer,
                                     SensorEventMapper sensorEventMapper) {
        super(kafkaProducer);
        this.sensorEventMapper = sensorEventMapper;
    }

    @Override
    public void handle(SensorEventProto event) {
        sendToKafka(event, sensorEventMapper::mapToAvro, sensorEventsTopic);
    }

    @Override
    public abstract SensorEventProto.PayloadCase getMessageType();
}
