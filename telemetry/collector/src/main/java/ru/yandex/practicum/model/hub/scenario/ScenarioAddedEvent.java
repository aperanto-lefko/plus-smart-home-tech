package ru.yandex.practicum.model.hub.scenario;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.enums.HubEventType;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.device.DeviceAction;

import java.util.List;


@ToString
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioAddedEvent extends HubEvent {
    @Size(min = 3, max = 2147483647)
    String name;
    List<ScenarioCondition> conditions;
    List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
