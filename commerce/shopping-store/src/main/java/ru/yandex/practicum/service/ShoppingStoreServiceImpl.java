package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.PageableMapper;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.dto.RemoveProductDto;
import ru.yandex.practicum.store.dto.UpdateQtyStateDto;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.dto.PageableDto;

import java.util.List;
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
    public List<ProductDto> getProductsByCategory(ProductCategory category, PageableDto pageableDto) {
        log.info("Поиск товара по категории {}", category);
        Pageable pageable = pageableMapper.toPageable(pageableDto);
        List<Product> products = productRepository.findByProductCategory(category, pageable).getContent();
        return products.stream()
                .map(productMapper::toDto)
                .toList();
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
    public Boolean removeProduct(RemoveProductDto removeProductDto) {
        UUID uuid = removeProductDto.getProductId();
        Product product = getProductById(uuid);
        log.info("Удаление товара с id {}", uuid);
        productRepository.delete(product);
        return true;
    }


    @Override
    @Transactional
    public Boolean updateQuantityState(UpdateQtyStateDto updateQtyStateDto) {
        log.info("Обновление состояния количества товара для id {}", updateQtyStateDto.getProductId());
        Product product = getProductById(updateQtyStateDto.getProductId());
        product.setQuantityState(updateQtyStateDto.getQuantityState());
        productRepository.save(product);
        return true;
    }
    @Override
    public ProductDto getProductDtoById(UUID uuid) {
        return productMapper.toDto(getProductById(uuid));
    }

    private Product getProductById(UUID uuid) {
        log.info("Поиск товара по id {}", uuid);
        return productRepository.findById(uuid)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден id " + uuid));
    }
}

