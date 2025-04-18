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
    public ResponseEntity<ShoppingCartDto> getShoppingCart(@RequestParam String userName) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(shoppingCartService.getShoppingCart(userName));
    }

    @PutMapping
    public ResponseEntity<ShoppingCartDto> addProducts(@RequestParam String userName,
                                                       @RequestBody Map<UUID, Integer> products) {
                return ResponseEntity
                .status(HttpStatus.OK)
                .body(shoppingCartService.addProducts(userName, products));
    }

    @DeleteMapping
    public ResponseEntity<Void> deactivateShoppingCart(@RequestParam String userName) {
        shoppingCartService.deactivateShoppingCart(userName);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/remove")
    public ResponseEntity<ShoppingCartDto> removeProducts(@RequestParam String userName,
                                                          @RequestBody List<UUID> productIds) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(shoppingCartService.removeProducts(userName, productIds));
    }

    @PostMapping("/change-quantity")
    public ResponseEntity<ShoppingCartDto> changeProductQuantity(@RequestParam String userName,
                                                                 @RequestBody ChangeProductQuantityRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(shoppingCartService.changeProductQuantity(userName, request));
    }


}
