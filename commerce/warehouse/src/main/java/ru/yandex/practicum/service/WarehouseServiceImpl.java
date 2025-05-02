package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.OrderBookingNoFoundException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.exception.WarehouseProductNotFoundException;
import ru.yandex.practicum.mapper.AddressMapper;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Dimension;
import ru.yandex.practicum.model.OrderBooking;
import ru.yandex.practicum.model.WarehouseItem;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseItemRepository;
import ru.yandex.practicum.repository.WarehouseOrderBookingRepository;
import ru.yandex.practicum.repository.WarehouseProductRepository;
import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.general_dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.warehouse.dto.WarehouseProductDto;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {
    WarehouseProductRepository warehouseProductRepository;
    WarehouseItemRepository warehouseItemRepository;
    WarehouseOrderBookingRepository warehouseOrderBookingRepository;
    WarehouseProductMapper warehouseProductMapper;

    AddressMapper addressMapper;
    static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};

    @Override
    @Transactional
    public void createProduct(WarehouseProductDto newProduct) {
        log.info("Проверка товара на складе");
        //id продукта приходит извне
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
        log.info("Проверка товаров из корзины существования на складе");
        Map<UUID, Integer> requestedProducts = shoppingCartDto.getProducts();
        return checkProductsList(requestedProducts);
    }


    @Override
    @Transactional
    public void addAndChangeQuantityProduct(AddProductToWarehouseRequest productRequest) {
        log.info("Добавление количества товара на складе по запросу {}", productRequest);
        UUID uuid = productRequest.getProductId();
        log.info("Поиск product по uuid {} в warehouse", uuid);
        WarehouseProduct product = warehouseProductRepository.findById(uuid)
                .orElseThrow(() -> new WarehouseProductNotFoundException(
                        String.format("Товар с ID %s не найден на складе", productRequest.getProductId())));
        log.info("Поиск item по uuid {} в warehouse или создание нового", uuid);
        WarehouseItem item = warehouseItemRepository.findByProduct_Id(uuid)
                .orElseGet(() -> WarehouseItem.builder()
                        .product(product)
                        .build());

        item.setQuantity(productRequest.getQuantity());
        warehouseItemRepository.save(item);
    }

    @Override
    public AddressDto getAddress() {
        log.info("Запрос адреса склада");
        int randomIndex = new SecureRandom().nextInt(ADDRESSES.length);
        Address address = Address.fromString(ADDRESSES[randomIndex]);
        return addressMapper.toDto(address);
    }

    @Override
    @Transactional
    public BookedProductsDto prepareOrderItemsForShipment(AssemblyProductsForOrderRequest request) {
        log.info("Подготовка товаров для заказа");
        Map<UUID, Integer> reservedProducts = request.getProducts();
        BookedProductsDto bookedProductsDto = checkProductsList(reservedProducts);
        OrderBooking orderBooking = OrderBooking.builder()
                .orderId(request.getOrderId())
                .products(reservedProducts)
                .deliveryVolume(bookedProductsDto.getDeliveryVolume())
                .deliveryWeight(bookedProductsDto.getDeliveryWeight())
                .fragile(bookedProductsDto.isFragile())
                .build();
        warehouseOrderBookingRepository.save(orderBooking);
        return bookedProductsDto;
    }

    @Override
    @Transactional
    public void returnProductToWarehouse(Map<UUID, Integer> products) {
        log.info("Переучет возвращаемых товаров на склад {}", products);
        List<WarehouseItem> items = warehouseItemRepository.findAllByProduct_IdIn(products.keySet());
        if (items.size() != products.size()) {
            throw new WarehouseProductNotFoundException("Один или несколько товаров не найдены на складе");
        }
        for (WarehouseItem item : items) {
            UUID productId = item.getProduct().getId();
            Integer returnedQuant = products.get(productId);
            if (returnedQuant == null || returnedQuant <= 0) {
                throw new IllegalArgumentException("Неверное количество для возврата товара " + returnedQuant);
            }
            item.setQuantity(item.getQuantity() + returnedQuant);
        }
        log.info("Сохранение обновленного списка товаров {}", items);
        warehouseItemRepository.saveAll(items);
    }

    @Override
    @Transactional
    public void sendProductsToDelivery(ShippedToDeliveryRequest request) {
        log.info("Оформление товаров в доставку");
        UUID orderId = request.getOrderId();
        OrderBooking orderBooking = warehouseOrderBookingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new OrderBookingNoFoundException("Запись orderBooking отсутствует на складе для заказа с id " + orderId));
        orderBooking.setDeliveryId(request.getDeliveryId());
        log.info("Сохранение обновленного orderBooking в базу {}", orderBooking);
        warehouseOrderBookingRepository.save(orderBooking);
    }


    private BookedProductsDto checkProductsList(Map<UUID, Integer> requestedProducts) {
        log.info("Проверка списка товаров по количеству на складе");
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
        if (items == null || items.size() != existingIds.size()) {
            throw new WarehouseProductNotFoundException("Продукты заведены на складе, но количество не задано, найденные WarehouseItem" + items);
        }
        //проверка соответствия количества
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

    private BookedProductsDto getBookedProduct(Map<UUID, Integer> requestedProducts, List<WarehouseItem> items) {

        log.info("Подсчет объема поставки для requestedProducts {} и найденных WarehouseItem {}", requestedProducts, items);
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

        boolean isFragile = items.stream()
                .map(WarehouseItem::getProduct)
                .anyMatch(WarehouseProduct::isFragile);
        return BookedProductsDto.builder()
                .fragile(isFragile)
                .deliveryVolume(deliveryVolume)
                .deliveryWeight(deliveryWeight)
                .build();
    }
}

