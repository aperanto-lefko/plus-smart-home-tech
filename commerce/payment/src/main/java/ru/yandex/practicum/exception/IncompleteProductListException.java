package ru.yandex.practicum.exception;

public class IncompleteProductListException extends RuntimeException {
    public IncompleteProductListException(String message) {
        super(message);
    }
}
