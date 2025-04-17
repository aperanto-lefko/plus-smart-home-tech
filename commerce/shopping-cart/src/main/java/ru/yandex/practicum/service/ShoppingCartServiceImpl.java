package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.exception.CartNotFoundException;
import ru.yandex.practicum.mapper.CartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class ShoppingCartServiceImpl implements ShoppingCartService {
    final ShoppingCartRepository shoppingCartRepository;
    final CartMapper cartMapper;

    @Override
    public ShoppingCartDto getShoppingCart(String userName) {
        log.info("Поиск корзины по имени пользователя {}", userName)
        ShoppingCart cart = findActiveCartByUserName(userName);
        return cartMapper.toDto(cart);
    }


    private ShoppingCart findActiveCartByUserName(String userName) {
        return shoppingCartRepository.findByUserNameAndActiveTrue(userName)
                .orElseThrow(() -> new CartNotFoundException("Корзина не найдена для пользователя " + userName));
    }
}
