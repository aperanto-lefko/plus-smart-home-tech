package ru.yandex.practicum.mapper;

import com.google.protobuf.Timestamp;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
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

public class HubMapper {
    public static DeviceActionAvro map(DeviceActionProto event) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(event.getSensorId())
                .setType(DeviceActionTypeAvro.valueOf(event.getType().name()))
                .setValue(event.getValue())
                .build();

    }

    public static ScenarioConditionAvro map(ScenarioConditionProto event) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(event.getSensorId())
                .setOperation(ConditionOperationTypeAvro.valueOf(event.getOperation().name()))
                .setType(ConditionTypeAvro.valueOf(event.getType().name()))
                .setValue(event.hasBoolValue() ? event.getBoolValue() : event.hasIntValue() ? event.getIntValue() : null)
                .build();
    }

    public static ScenarioAddedEventAvro map(ScenarioAddedEventProto event) {
        return ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setConditions(event.getConditionList().stream()
                        .map(HubMapper::map)
                        .toList())
                .setActions(event.getActionList().stream()
                        .map(HubMapper::map)
                        .toList())
                .build();
    }

    public static ScenarioRemovedEventAvro map(ScenarioRemovedEventProto event) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
    }

    public static DeviceAddedEventAvro map(DeviceAddedEventProto event) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setDeviceType(DeviceTypeAvro.valueOf(event.getType().name()))
                .build();
    }

    public static DeviceRemovedEventAvro map(DeviceRemovedEventProto event) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(event.getId())
                .build();
    }

    public static HubEventAvro mapScenarioAdded(HubEventProto event) {
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(map(event.getTimestamp()))
                .setEvent(map(event.getScenarioAdded()))
                .build();
    }

    public static HubEventAvro mapScenarioRemoved(HubEventProto event) {
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(map(event.getTimestamp()))
                .setEvent(map(event.getScenarioRemoved()))
                .build();
    }

    public static HubEventAvro mapDeviceAdded(HubEventProto event) {
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(map(event.getTimestamp()))
                .setEvent(map(event.getDeviceAdded()))
                .build();
    }

    public static HubEventAvro mapDeviceRemoved(HubEventProto event) {
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(map(event.getTimestamp()))
                .setEvent(map(event.getDeviceRemoved()))
                .build();
    }

    private static Instant map(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
