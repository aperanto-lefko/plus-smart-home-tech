package ru.yandex.practicum.hub_executor;

public interface RequestBuilder<T, R> {
    T build(R action, String hubId);
}
