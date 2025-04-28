package ru.yandex.practicum.cart.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;


import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart",
        path = "/api/v1/shopping-cart", fallback = ShoppingCartServiceFallback.class)
public interface ShoppingCartServiceClient {
    @GetMapping
    public ResponseEntity<ShoppingCartDto> getShoppingCart(@RequestParam String username);

    @PutMapping
    public ResponseEntity<ShoppingCartDto> addProducts(@RequestParam String username,
                                                       @RequestBody Map<UUID, Integer> products);

    @DeleteMapping
    public ResponseEntity<Void> deactivateShoppingCart(@RequestParam String username);

    @PostMapping("/remove")
    public ResponseEntity<ShoppingCartDto> removeProducts(@RequestParam String username,
                                                          @RequestBody List<UUID> productIds);

    @PostMapping("/change-quantity")
    public ResponseEntity<ShoppingCartDto> changeProductQuantity(@RequestParam String username,
                                                                 @RequestBody ChangeProductQuantityRequest request);
}
