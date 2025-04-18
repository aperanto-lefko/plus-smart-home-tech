package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.CartNotFoundException;
import ru.yandex.practicum.exception.NoProductInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.CartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
        validateUser(userName);
        log.info("Поиск корзины по имени пользователя {}", userName);
        ShoppingCart cart = getOrCreateCartForUser(userName);
        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addProducts(String userName, Map<UUID, Integer> products) {
        validateUser(userName);
        log.info("Добавление продуктов {} для пользователя {}", products, userName);
        ShoppingCart cart = getOrCreateCartForUser(userName);
        cart.setProducts(products);
        //проверка на складе
        //изменение количества на складе
        return cartMapper.toDto(shoppingCartRepository.save(cart));

    }

    @Override
    @Transactional
    public void deactivateShoppingCart(String userName) {
        validateUser(userName);
        ShoppingCart cart = getActiveShoppingCart(userName);
        log.info("Установка статуса \"неактивный\" для корзины");
        cart.setActive(false);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeProducts(String userName, List<UUID> productIds) {
        validateUser(userName);
        ShoppingCart cart = getActiveShoppingCart(userName);
        log.info("Удаление списка продуктов {} из корзины для пользователя {}", productIds, userName);
        cart.getProducts().keySet().removeIf(productIds::contains);
        //добавить изменение количества на складе
        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String userName, ChangeProductQuantityRequest request) {
        validateUser(userName);
        ShoppingCart cart = getActiveShoppingCart(userName);
        log.info("Изменение количества товаров для пользователя {} с запросом {}", userName, request);
        Map<UUID, Integer> products = cart.getProducts();
        Optional.ofNullable(products.get(request.getProductId()))
                .ifPresentOrElse(quantity -> products.put(request.getProductId(), request.getNewQuantity()),
                ()-> { throw new NoProductInShoppingCartException("При изменении количества товаров в " +
            "корзине товары в корзине не обнаружены" + request);}
                );
        //добавить изменение количества на складе
        cart.setProducts(products);
        return cartMapper.toDto(cart);
    }

    private ShoppingCart getOrCreateCartForUser(String userName) {
        log.info("Поиск корзины для пользователя {}", userName);
        return shoppingCartRepository.findByUserNameAndActiveTrue(userName)
                .orElseGet(() -> {
                    log.info("Создание новой корзины для пользователя");
                    return shoppingCartRepository.save(
                            ShoppingCart.builder()
                                    .userName(userName)
                                    .active(true)
                                    .products(new HashMap<>())
                                    .build()
                    );
                });
    }

    private ShoppingCart getActiveShoppingCart(String userName) {
        log.info("Поиск активной корзины для пользователя {}", userName);
        return shoppingCartRepository.findByUserNameAndActiveTrue(userName)
                .orElseThrow(() -> new CartNotFoundException("Корзина для пользователя не найдена"));
    }

    private void validateUser(String userName) {
        log.info("Проверка имени пользователя {}", userName);
        if (userName == null || userName.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }
    }
}
