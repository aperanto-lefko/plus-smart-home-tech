package ru.yandex.practicum.model.hub.scenario;

import jakarta.validation.constraints.Size;
import ru.yandex.practicum.enums.HubEventType;
import ru.yandex.practicum.model.hub.HubEvent;

public class ScenarioRemovedEvent extends HubEvent {

    @Override
    public HubEventType getType() {
        @Size(min = 3, max = 2147483647)
        String name;

        return HubEventType.SCENARIO_REMOVED;
    }
}
