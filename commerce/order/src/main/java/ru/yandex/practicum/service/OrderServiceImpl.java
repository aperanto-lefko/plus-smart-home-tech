package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.feign.ShoppingCartServiceClient;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.AddressMapper;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;
import ru.yandex.practicum.order.enums.OrderState;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.feign.WarehouseServiceClient;


import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    AddressMapper addressMapper;
    ShoppingCartServiceClient cartServiceClient;
    WarehouseServiceClient warehouseServiceClient;

    @Override
    public List<OrderDto> getClientOrders(String userName) {
        log.info("Поиск заказа для пользователя {}", userName);
        validateUser(userName);
        List<Order> list = orderRepository.findByUsername(userName);
        if (list == null || list.isEmpty()) {
            throw new NoOrderFoundException("Заказ для пользователя " + userName + "не найден");
        }
        return orderMapper.toDtoList(list);
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateNewOrderRequest request) {
        log.info("Создание нового заказа {}", request);
        BookedProductsDto bookedProductsDto = warehouseServiceClient.checkShoppingCart(request.getShoppingCartDto()).getBody();
        if (bookedProductsDto==null) {
            throw new
        }
        Order order = Order.builder()
                .shoppingCartId(request.getShoppingCartDto().getShoppingCartId())
                .state(OrderState.NEW)
                .products(request.getShoppingCartDto().getProducts())
                .deliveryWeight(bookedProductsDto.getDeliveryWeight())
                .deliveryVolume(bookedProductsDto.getDeliveryVolume())
                .fragile(bookedProductsDto.isFragile())
                .deliveryAddress(addressMapper.toEntity(request.getAddressDto()))
                .build();
        return null;
    }

    @Override
    @Transactional
    public OrderDto returnOrder(ProductReturnRequest request) {
        return null;
    }

    @Override
    @Transactional
    public OrderDto paymentOrder(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto paymentOrderFailed(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto deliveryOrder(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto deliveryOrderFailed(UUID orderId) {
        return null;
    }

    @Override
    @Transactional
    public OrderDto completeOrder(UUID orderId) {
        return null;
    }

    @Override
    @Transactional
    public OrderDto calculateTotalOrderCost(UUID orderId) {
        return null;
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryOrderCost(UUID orderId) {
        return null;
    }

    @Override
    @Transactional
    public OrderDto assemblyOrder(UUID orderId) {
        return null;
    }

    @Override
    @Transactional
    public OrderDto assemblyOrderFailed(UUID orderId) {
        return null;
    }

    private void validateUser(String userName) {
        log.info("Проверка имени пользователя {}", userName);
        if (userName == null || userName.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }
    }
}
