package ru.yandex.practicum.service;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.CartNotFoundException;
import ru.yandex.practicum.exception.NoProductInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.WarehouseServiceException;
import ru.yandex.practicum.mapper.CartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.feign.WarehouseServiceClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class ShoppingCartServiceImpl implements ShoppingCartService {
    final ShoppingCartRepository shoppingCartRepository;
    final CartMapper cartMapper;
    final WarehouseServiceClient warehouseServiceClient;
    final RedisTemplate<String, Object> redisTemplate;

    @Override
    public ShoppingCartDto getShoppingCart(String userName) {
        validateUser(userName);
        log.info("Поиск корзины по имени пользователя {}", userName);
        ShoppingCart cart = getOrCreateCartForUser(userName);
        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "warehouseService", fallbackMethod = "fallbackAddProducts")
    public ShoppingCartDto addProducts(String userName, Map<UUID, Integer> products) {
        validateUser(userName);
        log.info("Добавление продуктов {} для пользователя {}", products, userName);
        ShoppingCart cart = getOrCreateCartForUser(userName);
        cart.setProducts(products);
        log.info("Отправка проверки корзины (наличия товаров) склад");

        try {
            warehouseServiceClient.checkShoppingCart(cartMapper.toDto(cart));
        } catch (FeignException ex) {
            log.error("Сработал блок catch в FeignException");
            throw new WarehouseServiceException("Ошибка при проверке корзины на складе ");
        }
        //изменение количества на складе
        return cartMapper.toDto(shoppingCartRepository.save(cart));
    }

    public ShoppingCartDto fallbackAddProducts(String userName, Map<UUID, Integer> products, Exception ex) {
        log.error("Fallback Circuit для addProducts: {}", ex.getMessage());
        log.info("fallbackAddProducts добавление продуктов {} для пользователя {}", products, userName);
        ShoppingCart cart = getOrCreateCartForUser(userName);
        cart.setProducts(products);
        log.info("Сохранение в redis корзины и name пользователя");

        // Сохраняем всю корзину в Redis как JSON
        String cartKey = "cart.pending:" + cart.getShoppingCartId();
        redisTemplate.opsForValue().set(
                cartKey,
                cart,  // Преобразуем в DTO
                24, TimeUnit.HOURS
        );
        log.info("Корзина в базу данных со списком продуктов не сохранена");
        return null;
    }

    @Scheduled(fixedRate = 60000)  // Запуск каждые 60 секунд (но реальная задержка учитывается внутри)
    public void processPendingCarts() {
        log.info("Активирована фоновая отправка корзины на склад");
        Set<String> keys = redisTemplate.keys("cart.pending:*");
        if (keys == null || keys.isEmpty()) {
            return;
        }

        keys.forEach(key -> {
            String attemptsKey = null;
            try {
                // Получаем корзину
                ShoppingCart cart = (ShoppingCart) redisTemplate.opsForValue().get(key);
                if (cart == null) {
                    redisTemplate.delete(key);  // Если корзина исчезла, чистим ключ
                    return;
                }

                // Проверяем лимит попыток (макс. 3)
                attemptsKey = "cart.attempts:" + cart.getShoppingCartId();
                int attempts = getAttemptCount(attemptsKey);
                if (attempts >= 3) {
                    log.error("Корзина {} не обработана после 3 попыток. Удаление.", cart.getShoppingCartId());
                    redisTemplate.delete(key);
                    redisTemplate.delete(attemptsKey);
                    return;
                }

                // Проверяем задержку (экспоненциальная: 60s, 120s, 240s)
                String lastAttemptKey = "cart.last_attempt:" + cart.getShoppingCartId();
                long lastAttemptTime = getLastAttemptTime(lastAttemptKey);
                long delay = calculateDelay(attempts);  // 60s * 2^attempts
                if (System.currentTimeMillis() - lastAttemptTime < delay) {
                    log.info("Корзина {} ждет следующей попытки (через {} ms)", cart.getShoppingCartId(), delay);
                    return;  // Пропускаем, если не прошло нужное время
                }

                // Обновляем время последней попытки
                redisTemplate.opsForValue().set(lastAttemptKey, System.currentTimeMillis());

                // Пытаемся отправить на склад
                log.info("Попытка {} для корзины {}", attempts + 1, cart.getShoppingCartId());
                ResponseEntity<BookedProductsDto> response =
                        warehouseServiceClient.checkShoppingCart(cartMapper.toDto(cart));

                if (response.getStatusCode().is2xxSuccessful()) {
                    shoppingCartRepository.save(cart);
                    redisTemplate.delete(key);
                    redisTemplate.delete(attemptsKey);
                    redisTemplate.delete(lastAttemptKey);
                    log.info("Корзина {} успешно обработана!", cart.getShoppingCartId());
                } else {
                    incrementAttemptCount(attemptsKey);
                    log.error("Склад вернул ошибку для корзины {}", cart.getShoppingCartId());
                }
            } catch (Exception e) {
                // Feign-ошибка (таймаут/разрыв соединения) → увеличиваем счётчик
                if (attemptsKey != null) { // Проверяем, что attemptsKey был инициализирован
                    incrementAttemptCount(attemptsKey);
                    log.error("Ошибка связи со складом (попытка {}/3): {}",
                            getAttemptCount(attemptsKey), e.getMessage());
                } else {
                    log.error("Ошибка до инициализации attemptsKey: {}", e.getMessage());
                }
            }
        });
    }




    private int getAttemptCount(String attemptsKey) {
        Integer attempts = (Integer) redisTemplate.opsForValue().get(attemptsKey);
        return attempts == null ? 0 : attempts;
    }

    private long getLastAttemptTime(String lastAttemptKey) {
        Long lastAttempt = (Long) redisTemplate.opsForValue().get(lastAttemptKey);
        return lastAttempt == null ? 0 : lastAttempt;
    }

    private void incrementAttemptCount(String attemptsKey) {
        redisTemplate.opsForValue().increment(attemptsKey);
    }

    private long calculateDelay(int attempts) {
        return 60000 * (1L << attempts);  // 60s, 120s, 240s
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
                        () -> {
                            throw new NoProductInShoppingCartException("При изменении количества товаров в " +
                                    "корзине товары в корзине не обнаружены" + request);
                        }
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
