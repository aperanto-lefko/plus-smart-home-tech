package ru.yandex.practicum.mapper;

import com.google.protobuf.Timestamp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;


import java.time.Instant;


@Mapper(componentModel = "spring")
public interface HubEventMapper {
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "event", source = ".", qualifiedByName = "mapPayload")
    HubEventAvro mapToAvro(HubEventProto proto);

    @Named("mapPayload")
    default Object mapPayload(HubEventProto proto) {
        return switch (proto.getPayloadCase()) {
            case DEVICE_ADDED -> map(proto.getDeviceAdded());
            case DEVICE_REMOVED -> map(proto.getDeviceRemoved());
            case SCENARIO_ADDED -> map(proto.getScenarioAdded());
            case SCENARIO_REMOVED -> map(proto.getScenarioRemoved());
            case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Payload not set in HubEventProto");
        };
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "deviceType", source = "type")
    DeviceAddedEventAvro map(DeviceAddedEventProto proto);


    @Mapping(target = "id", source = "id")
    DeviceRemovedEventAvro map(DeviceRemovedEventProto proto);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "conditions", source = "conditionList")
    @Mapping(target = "actions", source = "actionList")
    ScenarioAddedEventAvro map(ScenarioAddedEventProto proto);

    @Mapping(target = "name", source = "name")
    ScenarioRemovedEventAvro map(ScenarioRemovedEventProto proto);


    @Mapping(target = "sensorId", source = "sensorId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "operation", source = "operation")
    @Mapping(target = "value", expression = "java(proto.hasIntValue() ? proto.getIntValue() : " +
            "proto.hasBoolValue() ? proto.getBoolValue() : null)")
    ScenarioConditionAvro map(ScenarioConditionProto proto);


    @Mapping(target = "sensorId", source = "sensorId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "value", expression = "java(proto.hasValue() ? proto.getValue() : null)")
    DeviceActionAvro map(DeviceActionProto proto);

    default DeviceTypeAvro map(DeviceTypeProto proto) {
        return DeviceTypeAvro.valueOf(proto.name());
    }

    default ConditionTypeAvro map(ConditionTypeProto proto) {
        return ConditionTypeAvro.valueOf(proto.name());
    }

    default ConditionOperationTypeAvro map(ConditionOperationProto proto) {
        return ConditionOperationTypeAvro.valueOf(proto.name());
    }

    default DeviceActionTypeAvro map(ActionTypeProto proto) {
        return DeviceActionTypeAvro.valueOf(proto.name());
    }

    default Instant mapToInstant(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
