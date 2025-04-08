package ru.yandex.practicum.exception;

public class DeSerealizationException extends RuntimeException {
    public DeSerealizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
