package ru.yandex.practicum.handler;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface SnapshotHandler<T> {
    void handle(T event);
}
