package ru.yandex.practicum.exception;

public class DeliveryServiceException extends RuntimeException {
    public DeliveryServiceException(String message) {
        super(message);
    }
}
