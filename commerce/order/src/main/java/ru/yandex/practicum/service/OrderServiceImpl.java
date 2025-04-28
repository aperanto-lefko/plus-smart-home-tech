package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {
    OrderRepository orderRepository;

    @Override
    public List<OrderDto> getClientOrders(String username) {
        return null;
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateNewOrderRequest request) {
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
    public OrderDto completeOrder(UUID orderId){
        return null;
    }
    @Override
    @Transactional
    public OrderDto calculateTotalOrderCost(UUID orderId) {
        return null;
    }
    @Override
    @Transactional
    public OrderDto calculateDeliveryOrderCost(UUID orderId){
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
}
