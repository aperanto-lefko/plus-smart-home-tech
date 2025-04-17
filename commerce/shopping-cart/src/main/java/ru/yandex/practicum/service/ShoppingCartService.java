package ru.yandex.practicum.service;

import ru.yandex.practicum.cart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart(String userName);
}
