package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryController {
    DeliveryService deliveryService;
    @PutMapping
    public ResponseEntity<DeliveryDto> createDelivery(@Valid @RequestBody DeliveryDto deliveryDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(deliveryService.createDelivery(deliveryDto));
    }
    @PostMapping("/successful")
    public ResponseEntity<Void> completeDelivery(@RequestBody UUID orderId) {
        deliveryService.completeDelivery(orderId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
    @PostMapping("/picked")
    public ResponseEntity<Void> pickupOrderForDelivery(@RequestBody UUID orderId) {
        deliveryService.pickupOrderForDelivery(orderId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
    @PostMapping("/failed")
    public ResponseEntity<Void> failDelivery(@RequestBody UUID orderId) {
        deliveryService.failDelivery(orderId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
    @PostMapping("/cost")
    public ResponseEntity<BigDecimal> calculateTotalCostDelivery(@Valid @RequestBody OrderDto orderDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(deliveryService.calculateTotalCostDelivery(orderDto));
    }


}
