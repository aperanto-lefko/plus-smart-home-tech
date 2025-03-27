package ru.yandex.practicum.exception;

public class NoHandlerException extends RuntimeException {
    public NoHandlerException(String message) {
        super(message);
    }
}
