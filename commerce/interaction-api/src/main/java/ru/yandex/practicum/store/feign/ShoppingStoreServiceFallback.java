package ru.yandex.practicum.store.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.ServiceUnavailableException;
import ru.yandex.practicum.store.dto.PageableDto;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.dto.UpdateQtyStateDto;
import ru.yandex.practicum.store.enums.ProductCategory;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class ShoppingStoreServiceFallback implements ShoppingStoreServiceClient {

    @Override
    public List<ProductDto> getProducts(ProductCategory category, PageableDto pageableDto) {
        log.warn("Активирован резервный вариант для getProducts с категорией: {}", category);
        throw new ServiceUnavailableException("Shopping store service недоступен");
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        log.warn("Активирован резервный вариант для createProduct с названием: {}", productDto.getProductName());
        throw new ServiceUnavailableException("Shopping store service недоступен");
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.warn("Активирован резервный вариант для updateProduct с id: {}", productDto.getProductId());
        throw new ServiceUnavailableException("Shopping store service недоступен");
    }

    @Override
    public Boolean removeProduct(UUID uuid) {
        log.warn("Активирован резервный вариант для removeProduct с id: {}", uuid);
        throw new ServiceUnavailableException("Shopping store service недоступен");
    }

    @Override
    public Boolean updateProductQuantityState(UpdateQtyStateDto updateQtyStateDto) {
        log.warn("Активирован резервный вариант для updateProductQuantityState с id: {}", updateQtyStateDto.getProductId());
        throw new ServiceUnavailableException("Shopping store service недоступен");
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        log.warn("FАктивирован резервный вариант для getProductById с id: {}", productId);
        throw new ServiceUnavailableException("Shopping store service недоступен");
    }

}
