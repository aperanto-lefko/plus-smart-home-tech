package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentServiceImpl implements PaymentService {

    @Override
    @Transactional
    public PaymentDto createPayment(OrderDto orderDto) {
        return null;
    }
    @Override
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        return null;
    }
    @Override
    public void refundPayment(UUID paymentId) {

    }
}
