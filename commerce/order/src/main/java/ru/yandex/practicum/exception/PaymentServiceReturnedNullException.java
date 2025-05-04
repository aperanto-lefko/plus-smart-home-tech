package ru.yandex.practicum.exception;

public class PaymentServiceReturnedNullException extends RuntimeException {
    public PaymentServiceReturnedNullException(String message) {
        super(message);
    }
}
