package ru.yandex.practicum.mapper;

import com.google.protobuf.Timestamp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassMapping;
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
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.device.DeviceAddedEvent;
import ru.yandex.practicum.model.hub.device.DeviceRemovedEvent;
import ru.yandex.practicum.model.hub.scenario.ScenarioAddedEvent;
import ru.yandex.practicum.model.hub.scenario.ScenarioRemovedEvent;

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

    // Маппинг для DeviceAdded
    @Mapping(target = "id", source = "id")
    @Mapping(target = "deviceType", source = "type")
    DeviceAddedEventAvro map(DeviceAddedEventProto proto);

    // Маппинг для DeviceRemoved
    @Mapping(target = "id", source = "id")
    DeviceRemovedEventAvro map(DeviceRemovedEventProto proto);

    // Маппинг для ScenarioAdded
    @Mapping(target = "name", source = "name")
    @Mapping(target = "conditions", source = "conditionList")
    @Mapping(target = "actions", source = "actionList")
    ScenarioAddedEventAvro map(ScenarioAddedEventProto proto);

    // Маппинг для ScenarioRemoved
    @Mapping(target = "name", source = "name")
    ScenarioRemovedEventAvro map(ScenarioRemovedEventProto proto);

    // Маппинг вложенных объектов Scenario
    @Mapping(target = "sensorId", source = "sensorId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "operation", source = "operation")
    @Mapping(target = "value", source = ".", qualifiedByName = "mapConditionValue")
    ScenarioConditionAvro map(ScenarioConditionProto proto);

    @Named("mapConditionValue")
    default Object mapConditionValue(ScenarioConditionProto proto) {
        return switch (proto.getValueCase()) {
            case BOOL_VALUE -> proto.getBoolValue();
            case INT_VALUE -> proto.getIntValue();
            case VALUE_NOT_SET -> null;
        };
    }

    @Mapping(target = "sensorId", source = "sensorId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "value", source = "value")
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
