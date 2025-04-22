package ru.yandex.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCartController {
    final ShoppingCartService shoppingCartService;

    @GetMapping
    public ResponseEntity<ShoppingCartDto> getShoppingCart(@RequestParam String username) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(shoppingCartService.getShoppingCart(username));
    }

    @PutMapping
    public ResponseEntity<ShoppingCartDto> addProducts(@RequestParam String username,
                                                       @RequestBody Map<UUID, Integer> products) {
                return ResponseEntity
                .status(HttpStatus.OK)
                .body(shoppingCartService.addProducts(username, products));
    }

    @DeleteMapping
    public ResponseEntity<Void> deactivateShoppingCart(@RequestParam String username) {
        shoppingCartService.deactivateShoppingCart(username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/remove")
    public ResponseEntity<ShoppingCartDto> removeProducts(@RequestParam String username,
                                                          @RequestBody List<UUID> productIds) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(shoppingCartService.removeProducts(username, productIds));
    }

    @PostMapping("/change-quantity")
    public ResponseEntity<ShoppingCartDto> changeProductQuantity(@RequestParam String username,
                                                                 @RequestBody ChangeProductQuantityRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(shoppingCartService.changeProductQuantity(username, request));
    }


}
