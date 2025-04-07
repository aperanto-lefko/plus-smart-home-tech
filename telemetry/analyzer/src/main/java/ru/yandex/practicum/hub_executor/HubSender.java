package ru.yandex.practicum.hub_executor;

public interface HubSender<T, E extends RuntimeException> {
    void send(T action, String hubId) throws E;
}
