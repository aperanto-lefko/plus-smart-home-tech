package ru.yandex.practicum.service;

import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.model.Scenario;

import java.util.List;

public interface ScenarioService {
    void addScenario(ScenarioAddedEventAvro event, String hubId);

    void removeScenario(String name, String hubId);

    List<Scenario> getScenariosByHubId(String hubId);
}
