package ru.yandex.practicum.model.hub.scenario;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.enums.HubEventType;
import ru.yandex.practicum.model.hub.device.DeviceAction;

import java.util.List;


@ToString
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioAddedEvent extends HubScenarioEvent {
    List<ScenarioCondition> conditions;
    List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
