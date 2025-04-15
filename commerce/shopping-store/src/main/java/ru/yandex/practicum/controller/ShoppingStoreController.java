package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.ShoppingStoreServiceImpl;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.model.PageableRequest;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingStoreController {
    final ShoppingStoreServiceImpl productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts(@RequestParam ProductCategory category,
                                                        @Valid @RequestParam PageableRequest pageableRequest) {
        return ResponseEntity.ok(productService.getProductsByCategory(category, pageableRequest));
    }

    @PutMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody @Valid ProductDto productDto) {
        return ResponseEntity.ok(productService.createProduct(productDto));
    }
    @PostMapping
    public ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(productDto));
    }

}
