package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;
import ru.yandex.practicum.model.ScenarioCondition;

@Mapper(componentModel = "spring", uses = {ConditionMapper.class, ActionMapper.class})
public interface ScenarioMapper {
    @Mapping(target = "hubId", source = "hubEvent.hub_id")
    @Mapping(target = "name", source = "hubEvent.event.name")
    @Mapping(target = "scenarioConditions", ignore = true)
    @Mapping(target = "scenarioActions", ignore = true)
    Scenario toScenario(HubEventAvro hubEvent);

    @Mapping(target = "sensor.id", source = "condition.sensorId")
    @Mapping(target = "condition", source = "condition")
    ScenarioCondition toScenarioCondition(ScenarioConditionAvro condition, Scenario scenario);

    @Mapping(target = "sensor.id", source = "action.sensorId")
    @Mapping(target = "action", source = "action")
    ScenarioAction toScenarioAction(DeviceActionAvro action, Scenario scenario);
}
