package ru.yandex.practicum.handler.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.handler.BaseSensorEventHandler;
import ru.yandex.practicum.mapper.SensorEventMapper;

@Component
public class ClimateSensorEventHandler extends BaseSensorEventHandler {
    @Autowired
    public ClimateSensorEventHandler(KafkaProducer<String, SpecificRecordBase> kafkaProducer,
                                     SensorEventMapper sensorEventMapper) {
        super(kafkaProducer, sensorEventMapper);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

}
