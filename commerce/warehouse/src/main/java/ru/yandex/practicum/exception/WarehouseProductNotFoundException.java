package ru.yandex.practicum.exception;

public class WarehouseProductNotFoundException extends RuntimeException {
    public WarehouseProductNotFoundException(String message) {
        super(message);
    }
}
