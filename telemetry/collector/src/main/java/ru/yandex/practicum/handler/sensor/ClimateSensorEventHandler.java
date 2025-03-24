package ru.yandex.practicum.handler.sensor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.handler.BaseEventHandler;
import ru.yandex.practicum.handler.SensorEventHandler;
import ru.yandex.practicum.mapper.SensorEventMapper;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClimateSensorEventHandler extends BaseEventHandler<SensorEventProto> implements SensorEventHandler {
    @Value("${kafka.topics.sensor_events_topic}")
    String sensorEventsTopic;
    final SensorEventMapper sensorEventMapper;

    public ClimateSensorEventHandler(KafkaProducer<String, SpecificRecordBase> kafkaProducer, SensorEventMapper sensorEventMapper) {
        super(kafkaProducer);
        this.sensorEventMapper = sensorEventMapper;
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        sendToKafka(event);
    }

    @Override
    protected String getTopic() {
        return sensorEventsTopic;
    }

    @Override
    protected SpecificRecordBase mapToAvro(SensorEventProto event) {
        return sensorEventMapper.mapToAvro(event);
    }
}
