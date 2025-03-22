package ru.yandex.practicum.model.hub.device;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.enums.DeviceActionType;

@ToString
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceAction {
    String sensorId;
    DeviceActionType type;
    Integer value;
}
