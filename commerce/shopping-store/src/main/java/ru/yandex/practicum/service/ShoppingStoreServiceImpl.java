package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.PageableMapper;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.dto.UpdateQtyStateDto;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.dto.PageableDto;
import ru.yandex.practicum.store.enums.ProductState;


import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Transactional(readOnly = true)
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    final ProductRepository productRepository;
    final PageableMapper pageableMapper;
    final ProductMapper productMapper;


    @Override
    public Page<ProductDto> getProductsByCategory(ProductCategory category, PageableDto pageableDto) {
        log.info("Поиск товара по категории {}", category);
        Pageable pageable = pageableMapper.toPageable(pageableDto);
        Page<Product> productPage = productRepository.findByProductCategory(category, pageable);
        return productPage.map(productMapper::toDto);
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        log.info("Создание нового товара {}", productDto);
        Product product = productRepository.save(productMapper.toEntity(productDto));
        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("Обновление товара {}", productDto);
        Product product = getProductById(productDto.getProductId());
        productMapper.updateProductFromDto(productDto, product);
        log.info("Сохранение обновленного товара {}", product);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional
    public Boolean removeProduct(UUID uuid) {
        Product product = getProductById(uuid);
        log.info("Изменение статуса товара с id {}", uuid);
        product.setProductState(ProductState.DEACTIVATE);
        log.info("Сохранение товара со статусом DEACTIVATE");
        return true;
    }


    @Override
    @Transactional
    public Boolean updateQuantityState(UpdateQtyStateDto updateQtyStateDto) {
        log.info("Обновление состояния количества товара для id {}", updateQtyStateDto.getProductId());
        Product product = getProductById(updateQtyStateDto.getProductId());
        product.setQuantityState(updateQtyStateDto.getQuantityState());
        return true;
    }


    @Override
    public ProductDto getProductDtoById(UUID productId) {
        return productMapper.toDto(getProductById(productId));
    }

    private Product getProductById(UUID uuid) {
        log.info("Поиск товара по id {}", uuid);
        return productRepository.findById(uuid)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден id " + uuid));
    }
}

