package ru.yandex.practicum.exception;

public class StoreServiceReturnedNullException extends RuntimeException {
    public StoreServiceReturnedNullException(String message) {
        super(message);
    }
}
