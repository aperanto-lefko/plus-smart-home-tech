package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@Valid @RequestBody OrderDto orderDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentService.createPayment(orderDto));
    }
    @PostMapping("/totalCost")
    public ResponseEntity<BigDecimal> calculateTotalCost(@Valid @RequestBody OrderDto orderDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentService.calculateTotalCost(orderDto));
    }
    @PostMapping("/refund")
    public ResponseEntity<Void> refundPayment(@RequestBody UUID paymentId) {
        paymentService.refundPayment(paymentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

}
