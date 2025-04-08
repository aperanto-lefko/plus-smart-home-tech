package ru.yandex.practicum.service;

import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.Condition;

import java.util.List;

public interface ConditionService {
    List<Condition> saveAll(List<ScenarioConditionAvro> conditions);
}
