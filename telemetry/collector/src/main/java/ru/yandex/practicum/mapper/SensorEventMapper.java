package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassMapping;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorEventAvro;
import ru.yandex.practicum.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.model.sensor.LightSensorEvent;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SwitchSensorEvent;
import ru.yandex.practicum.model.sensor.TemperatureSensorEvent;

@Mapper(componentModel = "spring")
public interface SensorEventMapper {
    @Mapping(target = "event", source = ".", qualifiedByName = "mapClass")
    SensorEventAvro mapToAvro (SensorEvent event);

    @Named("mapClass")
    @SubclassMapping(source = ClimateSensorEvent.class, target =  ClimateSensorEventAvro.class)
    @SubclassMapping(source = LightSensorEvent.class, target =  LightSensorEventAvro.class)
    @SubclassMapping(source = MotionSensorEvent.class, target =  MotionSensorEventAvro.class)
    @SubclassMapping(source = SwitchSensorEvent.class, target =  SwitchSensorEventAvro.class)
    @SubclassMapping(source = TemperatureSensorEvent.class, target =  TemperatureSensorEventAvro.class)
    Object mapClass(SensorEvent event);

    ClimateSensorEventAvro mapToAvro (ClimateSensorEvent event);
    LightSensorEventAvro mapToAvro (LightSensorEvent event);
    MotionSensorEventAvro mapToAvro (MotionSensorEvent event);
    SwitchSensorEventAvro mapToAvro (SwitchSensorEvent event);
    TemperatureSensorEventAvro mapToAvro (TemperatureSensorEvent event);
}
