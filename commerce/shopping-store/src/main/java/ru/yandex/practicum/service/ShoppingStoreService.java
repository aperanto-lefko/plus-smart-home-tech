package ru.yandex.practicum.service;

import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.dto.RemoveProductDto;
import ru.yandex.practicum.store.dto.UpdateQtyStateDto;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.dto.PageableDto;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreService {
    List<ProductDto> getProductsByCategory(ProductCategory category, PageableDto pageableDto);
    ProductDto createProduct(ProductDto productDto);
    ProductDto updateProduct(ProductDto productDto);
    Boolean removeProduct(RemoveProductDto removeProductDto);
    Boolean updateQuantityState(UpdateQtyStateDto updateQtyStateDto);
    ProductDto getProductDtoById(UUID uuid);
}
