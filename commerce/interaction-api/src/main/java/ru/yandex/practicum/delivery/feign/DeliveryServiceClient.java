package ru.yandex.practicum.delivery.feign;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.order.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryServiceClient {
    @PutMapping
    public ResponseEntity<DeliveryDto> createDelivery(@Valid @RequestBody DeliveryDto deliveryDto);

    @PostMapping("/successful")
    public ResponseEntity<Void> completeDelivery(@RequestBody UUID orderId);

    @PostMapping("/picked")
    public ResponseEntity<Void> pickupOrderForDelivery(@RequestBody UUID orderId);

    @PostMapping("/failed")
    public ResponseEntity<Void> failDelivery(@RequestBody UUID orderId);

    @PostMapping("/cost")
    public ResponseEntity<BigDecimal> calculateTotalCostDelivery(@Valid @RequestBody OrderDto orderDto);
}
