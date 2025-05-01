package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getClientOrders(@RequestParam String userName) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.getClientOrders(userName));
    }

    @PutMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateNewOrderRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.createOrder(request));
    }

    @PostMapping("/return")
    public ResponseEntity<OrderDto> returnOrder(@Valid @RequestBody ProductReturnRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.returnOrder(request));
    }
    @PostMapping("/payment")
    public ResponseEntity<OrderDto> paymentOrder(@RequestBody UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.paymentOrder(orderId));
    }
    @PostMapping("/payment/failed")
    public ResponseEntity<OrderDto> paymentOrderFailed(@RequestBody UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.paymentOrderFailed(orderId));
    }
    @PostMapping("/delivery")
    public ResponseEntity<OrderDto> deliveryOrder(@RequestBody UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.deliveryOrder(orderId));
    }
    @PostMapping("/delivery/failed")
    public ResponseEntity<OrderDto> deliveryOrderFailed(@RequestBody UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.deliveryOrderFailed(orderId));
    }
    @PostMapping("/completed")
    public ResponseEntity<OrderDto> completeOrder(@RequestBody UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.completeOrder(orderId));
    }
    @PostMapping("/calculate/total")
    public ResponseEntity<OrderDto> calculateTotalOrderCost(@RequestBody UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.calculateTotalOrderCost(orderId));
    }
    @PostMapping("/calculate/delivery")
    public ResponseEntity<OrderDto> calculateDeliveryOrderCost(@RequestBody UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.calculateDeliveryOrderCost(orderId));
    }
    @PostMapping("/assembly")
    public ResponseEntity<OrderDto> assemblyOrder(@RequestBody UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.assemblyOrder(orderId));
    }
    @PostMapping("/assembly/failed")
    public ResponseEntity<OrderDto> assemblyOrderFailed(@RequestBody UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.assemblyOrderFailed(orderId));
    }
}
