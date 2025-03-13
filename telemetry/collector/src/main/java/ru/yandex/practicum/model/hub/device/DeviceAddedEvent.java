package ru.yandex.practicum.model.hub.device;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.enums.DeviceType;
import ru.yandex.practicum.enums.HubEventType;

@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceAddedEvent extends HubDeviceEvent{
    DeviceType deviceType;

    @Override
    public HubEventType getType(){
        return HubEventType.DEVICE_ADDED;
    }
}
