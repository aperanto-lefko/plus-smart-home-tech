package ru.yandex.practicum.service;

import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.order.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {
    DeliveryDto createDelivery(DeliveryDto deliveryDto);
    void completeDelivery(UUID orderId);
    void pickupOrderForDelivery(UUID orderId);
    void failDelivery(UUID orderId);
    BigDecimal calculateTotalCostDelivery(OrderDto orderDto);
}
