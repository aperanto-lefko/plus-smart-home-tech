package ru.yandex.practicum.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.handler.SensorEventHandler;
@Component
public class TemperatureSensorEventHandler implements SensorEventHandler {
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
    }
}
