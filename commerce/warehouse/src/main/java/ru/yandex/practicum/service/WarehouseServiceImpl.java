package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseProductRepository;
import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.dto.NewProductInWareHouseRequest;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {
    final WarehouseProductRepository warehouseProductRepository;
    final WarehouseProductMapper warehouseProductMapper;

    @Override
    @Transactional
    public void createProduct(NewProductInWareHouseRequest newProduct) {
        log.info("Проверка товара на складе");
        if (warehouseProductRepository.existsById(newProduct.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException(
                    String.format("Товар с ID %s уже существует на складе", newProduct.getProductId()));
        }
        log.info("Добавление нового товара на склад {}", newProduct);
        WarehouseProduct product = warehouseProductMapper.toEntity(newProduct);
        warehouseProductRepository.save(product);
    }

    @Override
    public BookedProductsDto checkShoppingCart(ShoppingCartDto shoppingCartDto) {
        return null;
    }

    @Override
    public void addAndChangeQuantityProduct(AddProductToWarehouseRequest productRequest) {

    }

    @Override
    public AddressDto getAddress() {
        return null;

    }
}
