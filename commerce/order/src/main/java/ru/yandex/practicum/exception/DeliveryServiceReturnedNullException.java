package ru.yandex.practicum.exception;

public class DeliveryServiceReturnedNullException extends RuntimeException {
    public DeliveryServiceReturnedNullException(String message) {
        super(message);
    }
}
