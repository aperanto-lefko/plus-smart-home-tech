package ru.yandex.practicum.hub_executor;

public interface HubExecutor<T,E extends RuntimeException> {
    void execute(T action, String hubId) throws E;
}
