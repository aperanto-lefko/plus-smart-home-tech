package ru.yandex.practicum.cart.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.ServiceUnavailableException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class ShoppingCartServiceFallback {
    public ResponseEntity<ShoppingCartDto> getShoppingCart(String username) {
        log.warn("Активирован резервный вариант для getShoppingCart для пользователя {} ", username);
        throw new ServiceUnavailableException("ShoppingCart service недоступен");
    }

    public ResponseEntity<ShoppingCartDto> addProducts(@RequestParam String username,
                                                       @RequestBody Map<UUID, Integer> products) {
        log.warn("Активирован резервный вариант для  addProducts для пользователя {} ", username);
        throw new ServiceUnavailableException("ShoppingCart service недоступен");
    }


    public ResponseEntity<Void> deactivateShoppingCart(@RequestParam String username) {
        log.warn("Активирован резервный вариант для deactivateShoppingCart для пользователя {} ", username);
        throw new ServiceUnavailableException("ShoppingCart service недоступен");
    }


    public ResponseEntity<ShoppingCartDto> removeProducts(@RequestParam String username,
                                                          @RequestBody List<UUID> productIds) {
        log.warn("Активирован резервный вариант для removeProducts для пользователя {} ", username);
        throw new ServiceUnavailableException("ShoppingCart service недоступен");
    }


    public ResponseEntity<ShoppingCartDto> changeProductQuantity(@RequestParam String username,
                                                                 @RequestBody ChangeProductQuantityRequest request) {
        log.warn("Активирован резервный вариант для changeProductQuantity для пользователя {} ", username);
        throw new ServiceUnavailableException("ShoppingCart service недоступен");
    }
}
