package ru.yandex.practicum.mapper;

import com.google.protobuf.Timestamp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorEventAvro;

import java.time.Instant;


@Mapper(componentModel = "spring")
public interface SensorEventMapper {
     @Mapping(target = "id", source = "id")
   @Mapping(target = "hubId", source = "hubId")
   @Mapping(target = "timestamp", source = "timestamp")
   @Mapping(target = "event", source = ".", qualifiedByName = "mapPayload")
   SensorEventAvro mapToAvro(SensorEventProto proto);

    @Named("mapPayload")
    default Object mapPayload(SensorEventProto proto) {
        return switch (proto.getPayloadCase()) {
            case MOTION_SENSOR_EVENT -> map(proto.getMotionSensorEvent());
            case TEMPERATURE_SENSOR_EVENT -> map(proto.getTemperatureSensorEvent());
            case LIGHT_SENSOR_EVENT -> map(proto.getLightSensorEvent());
            case CLIMATE_SENSOR_EVENT -> map(proto.getClimateSensorEvent());
            case SWITCH_SENSOR_EVENT -> map(proto.getSwitchSensorEvent());
            case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Payload not set in SensorEventProto");
        };
    }

    @Mapping(target = "linkQuality", source = "linkQuality")
    @Mapping(target = "motion", source = "motion")
    @Mapping(target = "voltage", source = "voltage")
    MotionSensorEventAvro map(MotionSensorProto proto);

    @Mapping(target = "temperatureC", source = "temperatureC")
    @Mapping(target = "temperatureF", source = "temperatureF")
    TemperatureSensorEventAvro map(TemperatureSensorProto proto);

    @Mapping(target = "linkQuality", source = "linkQuality")
    @Mapping(target = "luminosity", source = "luminosity")
    LightSensorEventAvro map(LightSensorProto proto);

    @Mapping(target = "temperatureC", source = "temperatureC")
    @Mapping(target = "humidity", source = "humidity")
    @Mapping(target = "co2Level", source = "co2Level")
    ClimateSensorEventAvro map(ClimateSensorProto proto);

    @Mapping(target = "state", source = "state")
    SwitchSensorEventAvro map(SwitchSensorProto proto);

    default Instant mapToInstant(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
    }
