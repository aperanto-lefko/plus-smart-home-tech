package ru.yandex.practicum.exception;

public class WarehouseServiceReturnedNullException extends RuntimeException {
    public WarehouseServiceReturnedNullException(String message) {
        super(message);
    }
}
