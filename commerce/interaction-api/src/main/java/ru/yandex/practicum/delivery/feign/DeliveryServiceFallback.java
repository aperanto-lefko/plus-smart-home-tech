package ru.yandex.practicum.delivery.feign;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.exception.ServiceUnavailableException;
import ru.yandex.practicum.order.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@Slf4j
public class DeliveryServiceFallback implements DeliveryServiceClient {
    @Override
    public ResponseEntity<DeliveryDto> createDelivery(DeliveryDto deliveryDto) {
        log.warn("Активирован резервный вариант для createDelivery для доставки {} ", deliveryDto);
        throw new ServiceUnavailableException("Delivery service недоступен");
    }

    @Override
    public ResponseEntity<Void> completeDelivery(@RequestBody UUID orderId) {
        log.warn("Активирован резервный вариант для createDelivery для заказа с id {} ", orderId);
        throw new ServiceUnavailableException("Delivery service недоступен");
    }

    @Override
    public ResponseEntity<Void> pickupOrderForDelivery(@RequestBody UUID orderId) {
        log.warn("Активирован резервный вариант для pickupOrderForDelivery для заказа с id {} ", orderId);
        throw new ServiceUnavailableException("Delivery service недоступен");
    }

    @Override
    public ResponseEntity<Void> failDelivery(@RequestBody UUID orderId) {
        log.warn("Активирован резервный вариант для failDelivery для заказа с id {} ", orderId);
        throw new ServiceUnavailableException("Delivery service недоступен");
    }

    @Override
    public ResponseEntity<BigDecimal> calculateTotalCostDelivery(@Valid @RequestBody OrderDto orderDto) {
        log.warn("Активирован резервный вариант для calculateTotalCostDelivery для заказа с id {} ", orderDto);
        throw new ServiceUnavailableException("Delivery service недоступен");
    }
}
