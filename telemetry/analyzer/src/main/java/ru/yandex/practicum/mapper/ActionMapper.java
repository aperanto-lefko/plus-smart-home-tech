package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionTypeAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.DeviceActionType;

@Mapper(componentModel = "spring")
public interface ActionMapper {
    @Mapping(target = "type", source = "action.type")
    @Mapping(target = "value", source = "action.value")
    Action toAction(DeviceActionAvro action);

    default DeviceActionType map(DeviceActionTypeAvro type) {
        return DeviceActionType.valueOf(type.name());
    }
}
