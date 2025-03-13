package ru.yandex.practicum.model.hub.scenario;

import ru.yandex.practicum.enums.HubEventType;

public class ScenarioRemovedEvent extends HubScenarioEvent {

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
