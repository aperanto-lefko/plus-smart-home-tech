package ru.yandex.practicum.handler;

public interface SnapshotHandler<T> {
    void handle(T event);
}
