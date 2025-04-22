package ru.yandex.practicum.exception;

public class NoProductInShoppingCartException extends RuntimeException {
    public NoProductInShoppingCartException(String message) {
        super(message);
    }
}
