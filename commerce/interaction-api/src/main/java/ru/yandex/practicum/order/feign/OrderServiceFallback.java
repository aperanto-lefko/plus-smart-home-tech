package ru.yandex.practicum.order.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.ServiceUnavailableException;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class OrderServiceFallback implements OrderServiceClient {

    @Override
    public ResponseEntity<List<OrderDto>> getClientOrders(String userName) {
        log.warn("Активирован резервный вариант для getClientOrders пользователя: {}", userName);
        throw new ServiceUnavailableException("Order service недоступен");
    }

    @Override
    public ResponseEntity<OrderDto> createOrder(CreateNewOrderRequest request) {
        log.warn("Активирован резервный вариант для createOrder");
        throw new ServiceUnavailableException("Order service недоступен");
    }

    @Override
    public ResponseEntity<OrderDto> returnOrder(ProductReturnRequest request) {
        log.warn("Активирован резервный вариант для returnOrder заказа: {}", request.getOrderId());
        throw new ServiceUnavailableException("Order service недоступен");
    }

    @Override
    public ResponseEntity<OrderDto> paymentOrder(UUID orderId) {
        log.warn("Активирован резервный вариант для paymentOrder заказа: {}", orderId);
        throw new ServiceUnavailableException("Order service недоступен");
    }

    @Override
    public ResponseEntity<OrderDto> paymentOrderFailed(UUID orderId) {
        log.warn("Активирован резервный вариант для paymentOrderFailed заказа: {}", orderId);
        throw new ServiceUnavailableException("Order service недоступен");
    }

    @Override
    public ResponseEntity<OrderDto> deliveryOrder(UUID orderId) {
        log.warn("Активирован резервный вариант для deliveryOrder заказа: {}", orderId);
        throw new ServiceUnavailableException("Order service недоступен");
    }

    @Override
    public ResponseEntity<OrderDto> deliveryOrderFailed(UUID orderId) {
        log.warn("Активирован резервный вариант для deliveryOrderFailed заказа: {}", orderId);
        throw new ServiceUnavailableException("Order service недоступен");
    }

    @Override
    public ResponseEntity<OrderDto> completeOrder(UUID orderId) {
        log.warn("Активирован резервный вариант для completeOrder заказа: {}", orderId);
        throw new ServiceUnavailableException("Order service недоступен");
    }

    @Override
    public ResponseEntity<OrderDto> calculateTotalOrderCost(UUID orderId) {
        log.warn("Активирован резервный вариант для calculateTotalOrderCost заказа: {}", orderId);
        throw new ServiceUnavailableException("Order service недоступен");
    }

    @Override
    public ResponseEntity<OrderDto> calculateDeliveryOrderCost(UUID orderId) {
        log.warn("Активирован резервный вариант для calculateDeliveryOrderCost заказа: {}", orderId);
        throw new ServiceUnavailableException("Order service недоступен");
    }

    @Override
    public ResponseEntity<OrderDto> assemblyOrder(UUID orderId) {
        log.warn("Активирован резервный вариант для assemblyOrder заказа: {}", orderId);
        throw new ServiceUnavailableException("Order service недоступен");
    }

    @Override
    public ResponseEntity<OrderDto> assemblyOrderFailed(UUID orderId) {
        log.warn("Активирован резервный вариант для assemblyOrderFailed заказа: {}", orderId);
        throw new ServiceUnavailableException("Order service недоступен");
    }
}
