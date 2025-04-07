package ru.yandex.practicum.exception;

public class ActionProcessingException extends RuntimeException {
    public ActionProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
