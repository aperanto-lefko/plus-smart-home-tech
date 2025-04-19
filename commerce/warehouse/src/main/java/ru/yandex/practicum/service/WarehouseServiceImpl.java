package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.exception.WarehouseProductNotFoundException;
import ru.yandex.practicum.mapper.AddressMapper;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Dimension;
import ru.yandex.practicum.model.WarehouseItem;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseItemRepository;
import ru.yandex.practicum.repository.WarehouseProductRepository;
import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.dto.WarehouseProductDto;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {
    final WarehouseProductRepository warehouseProductRepository;
    final WarehouseItemRepository warehouseItemRepository;
    final WarehouseProductMapper warehouseProductMapper;
    final AddressMapper addressMapper;
    static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};

    @Override
    @Transactional
    public void createProduct(WarehouseProductDto newProduct) {
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
        Map<UUID, Integer> requestedProducts = shoppingCartDto.getProducts();
        // Проверяем отсутствующие товары
        log.info("Проверка товаров из корзины существования на складе");
        Set<UUID> existingIds = warehouseProductRepository.findAllById(requestedProducts.keySet())
                .stream()
                .map(WarehouseProduct::getId)
                .collect(Collectors.toSet());

        List<UUID> missingProducts = requestedProducts.keySet().stream()
                .filter(id -> !existingIds.contains(id))
                .toList();

        if (!missingProducts.isEmpty()) {
            throw new WarehouseProductNotFoundException("Товары отсутствуют на складе: " + missingProducts);
        }
        log.info("Проверка товаров по количеству на складе");
        List<WarehouseItem> items = warehouseItemRepository.findAllByProduct_IdIn(requestedProducts.keySet());
        Map<UUID, Integer> insufficientProducts = items.stream()
                .filter(item -> {
                    Integer requested = requestedProducts.get(item.getProduct().getId());
                    return requested != null && item.getQuantity() < requested;
                })
                .collect(Collectors.toMap(
                        item -> item.getProduct().getId(),
                        WarehouseItem::getQuantity
                ));

        if (!insufficientProducts.isEmpty()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse("Не хватает на складе товаров: " + insufficientProducts);
        }
        return getBookedProduct(requestedProducts, items);
    }


    @Override
    @Transactional
    public void addAndChangeQuantityProduct(AddProductToWarehouseRequest productRequest) {
        log.info("Добавление количества товара на складе по запросу {}", productRequest);
        UUID uuid = productRequest.getProductId();
        if(!warehouseProductRepository.existsById(uuid)){
               throw new WarehouseProductNotFoundException(
                        String.format("Товар с ID %s не найден на складе", productRequest.getProductId()));}
        WarehouseItem item = warehouseItemRepository.findByProduct_Id(uuid)
                        .orElseGet(WarehouseItem::new);
        item.setQuantity(productRequest.getQuantity());
    }

    @Override
    public AddressDto getAddress() {
        log.info("Запрос адреса склада");
        Address address = Address.fromString(ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)]);
        return addressMapper.toDto(address);
    }

    private BookedProductsDto getBookedProduct(Map<UUID, Integer> requestedProducts, List<WarehouseItem> items) {
        log.info("Подсчет объема поставки");
        double deliveryWeight = items.stream()
                .mapToDouble(item -> {
                    int requestedQty = requestedProducts.get(item.getProduct().getId());
                    return item.getProduct().getWeight() * requestedQty;
                })
                .sum();

        double deliveryVolume = items.stream()
                .mapToDouble(item -> {
                    int requestedQty = requestedProducts.get(item.getProduct().getId());
                    Dimension dim = item.getProduct().getDimension();
                    return dim.getWidth() * dim.getHeight() * dim.getDepth() * requestedQty;
                })
                .sum();

        boolean fragile = items.stream()
                .map(WarehouseItem::getProduct)
                .anyMatch(WarehouseProduct::isFragile);
        return BookedProductsDto.builder()
                .fragile(fragile)
                .deliveryVolume(deliveryVolume)
                .deliveryWeight(deliveryWeight)
                .build();
    }
}
