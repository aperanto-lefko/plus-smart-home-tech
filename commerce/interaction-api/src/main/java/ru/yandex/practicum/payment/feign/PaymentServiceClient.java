package ru.yandex.practicum.payment.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;


@FeignClient(name = "payment",
        path = "/api/v1/payment",
        fallback = PaymentServiceFallback.class,
        configuration = PaymentFeignConfig.class)
public interface PaymentServiceClient {
    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@Valid @RequestBody OrderDto orderDto);

    @PostMapping("/totalCost")
    public ResponseEntity<BigDecimal> calculateTotalCost(@Valid @RequestBody OrderDto orderDto);

    @PostMapping("/refund")
    public ResponseEntity<Void> refundPayment(@RequestBody UUID paymentId);

    @PostMapping("/productCost")
    public ResponseEntity<BigDecimal> calculateProductCost(@RequestBody OrderDto orderDto);

    @PostMapping("/failed")
    public ResponseEntity<Void> failPayment(@RequestBody UUID paymentId);
}
