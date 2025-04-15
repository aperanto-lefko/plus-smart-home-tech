package ru.yandex.practicum.service;

import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.model.PageableRequest;

import java.util.List;

public interface ShoppingStoreService {
    List<ProductDto> getProductsByCategory(ProductCategory category, PageableRequest pageableRequest);
}
