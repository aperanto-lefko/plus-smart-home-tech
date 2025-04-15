package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.ShoppingStoreServiceImpl;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.dto.RemoveProductDto;
import ru.yandex.practicum.store.dto.UpdateQtyStateDto;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.dto.PageableDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingStoreController {
    final ShoppingStoreServiceImpl productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts(@RequestParam ProductCategory category,
                                                        @Valid @RequestParam PageableDto pageableDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getProductsByCategory(category, pageableDto));
    }

    @PutMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody @Valid ProductDto productDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.createProduct(productDto));
    }

    @PostMapping
    public ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto productDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.updateProduct(productDto));
    }

    @PostMapping("/removeProductFromStore")
    public ResponseEntity<Boolean> removeProduct(@RequestBody @Valid RemoveProductDto productRemoveDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.removeProduct(productRemoveDto));
    }
    @PostMapping("/quantityState")
    public ResponseEntity<Boolean> updateProductQuantityState(@RequestBody @Valid UpdateQtyStateDto updateQtyStateDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.updateQuantityState(updateQtyStateDto));
    }
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID uuid) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getProductDtoById(uuid));
    }
}
