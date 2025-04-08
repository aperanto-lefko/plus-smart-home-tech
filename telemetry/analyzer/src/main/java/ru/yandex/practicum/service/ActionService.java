package ru.yandex.practicum.service;

import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.model.Action;

import java.util.List;

public interface ActionService {
    List<Action> saveAll(List<DeviceActionAvro> actions);
}
