package ru.yandex.practicum.exception;

public class WarehouseServiceException extends RuntimeException {
    public WarehouseServiceException(String message) {
        super(message);
    }
}
