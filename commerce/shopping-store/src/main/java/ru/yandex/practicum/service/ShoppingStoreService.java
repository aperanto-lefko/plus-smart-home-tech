package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.dto.UpdateQtyStateDto;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.dto.PageableDto;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreService {
    Page<ProductDto> getProductsByCategory(ProductCategory category, PageableDto pageableDto);
    ProductDto createProduct(ProductDto productDto);
    ProductDto updateProduct(ProductDto productDto);
    Boolean removeProduct(UUID uuid);
    Boolean updateQuantityState(UpdateQtyStateDto updateQtyStateDto);
    ProductDto getProductDtoById(UUID uuid);
}
