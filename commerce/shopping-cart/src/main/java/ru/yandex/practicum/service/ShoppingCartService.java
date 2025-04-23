package ru.yandex.practicum.service;


import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart(String userName);
    ShoppingCartDto addProducts(String userName, Map<UUID, Integer> products);
    void deactivateShoppingCart(String userName);
    ShoppingCartDto removeProducts(String userName, List<UUID> productIds);
    ShoppingCartDto changeProductQuantity(String userName, ChangeProductQuantityRequest request);

}
