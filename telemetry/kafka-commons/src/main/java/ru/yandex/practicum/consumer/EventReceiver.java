package ru.yandex.practicum.consumer;

public interface EventReceiver<V> {
    void handle(V record);
}
