package ru.yandex.practicum.payment.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.exception.ServiceUnavailableException;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@Slf4j
public class PaymentServiceFallback implements PaymentServiceClient {

    @Override
    public ResponseEntity<PaymentDto> createPayment(OrderDto orderDto) {
        log.warn("Активирован резервный вариант для createPayment для заказа {} ", orderDto);
        throw new ServiceUnavailableException("Payment service недоступен");
    }

    @Override
    public ResponseEntity<BigDecimal> calculateTotalCost(OrderDto orderDto) {
        log.warn("Активирован резервный вариант для calculateTotalCost для заказа {} ", orderDto);
        throw new ServiceUnavailableException("Payment service недоступен");
    }

    @Override
    public ResponseEntity<Void> refundPayment(UUID paymentId) {
        log.warn("Активирован резервный вариант для refundPayment для paymentId {} ", paymentId);
        throw new ServiceUnavailableException("Payment service недоступен");
    }

    @Override
    public ResponseEntity<BigDecimal> calculateProductCost(OrderDto orderDto) {
        log.warn("Активирован резервный вариант для calculateProductCost для заказа {} ", orderDto);
        throw new ServiceUnavailableException("Payment service недоступен");
    }

    @Override
    public ResponseEntity<Void> failPayment(UUID paymentId) {
        log.warn("Активирован резервный вариант для failPayment для  paymentId {} ", paymentId);
        throw new ServiceUnavailableException("Payment service недоступен");
    }
}
