package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.order.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryServiceImpl implements DeliveryService {

    @Override
    @Transactional
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        return null;
    }

    @Override
    public void completeDelivery(UUID orderId) {

    }

    @Override
    public void pickupOrderForDelivery(UUID orderId) {
    }

    @Override
    public void failDelivery(UUID orderId) {

    }
    @Override
    public BigDecimal calculateTotalCostDelivery(OrderDto orderDto) {
        return null;
    }
}
