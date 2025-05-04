package ru.yandex.practicum.exception;

public class OrderBookingNoFoundException extends RuntimeException {
    public OrderBookingNoFoundException(String message) {
        super(message);
    }
}
