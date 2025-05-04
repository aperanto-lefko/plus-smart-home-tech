package ru.yandex.practicum.service;

import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<OrderDto> getClientOrders(String username);
    OrderDto createOrder(CreateNewOrderRequest request);
    OrderDto returnOrder(ProductReturnRequest request);
    OrderDto paymentOrder(UUID orderId);
    OrderDto paymentOrderFailed(UUID orderId);
    OrderDto deliveryOrder(UUID orderId);
    OrderDto deliveryOrderFailed(UUID orderId);
    OrderDto completeOrder(UUID orderId);
    OrderDto calculateTotalOrderCost(UUID orderId);
    OrderDto calculateDeliveryOrderCost(UUID orderId);
    OrderDto assemblyOrder(UUID orderId);
    OrderDto assemblyOrderFailed(UUID orderId);

}
