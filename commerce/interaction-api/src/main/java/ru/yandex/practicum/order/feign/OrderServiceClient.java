package ru.yandex.practicum.order.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order",
        path = "/api/v1/order",
        fallback = OrderServiceFallback.class,
        configuration = OrderFeignConfig.class)
public interface OrderServiceClient {

    @GetMapping
    ResponseEntity<List<OrderDto>> getClientOrders(@RequestParam String userName);

    @PutMapping
    ResponseEntity<OrderDto> createOrder(@RequestBody @Valid CreateNewOrderRequest request);

    @PostMapping("/return")
    ResponseEntity<OrderDto> returnOrder(@RequestBody @Valid ProductReturnRequest request);

    @PostMapping("/payment")
    ResponseEntity<OrderDto> paymentOrder(@RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    ResponseEntity<OrderDto> paymentOrderFailed(@RequestBody UUID orderId);

    @PostMapping("/delivery")
    ResponseEntity<OrderDto> deliveryOrder(@RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    ResponseEntity<OrderDto> deliveryOrderFailed(@RequestBody UUID orderId);

    @PostMapping("/completed")
    ResponseEntity<OrderDto> completeOrder(@RequestBody UUID orderId);

    @PostMapping("/calculate/total")
    ResponseEntity<OrderDto> calculateTotalOrderCost(@RequestBody UUID orderId);

    @PostMapping("/calculate/delivery")
    ResponseEntity<OrderDto> calculateDeliveryOrderCost(@RequestBody UUID orderId);

    @PostMapping("/assembly")
    ResponseEntity<OrderDto> assemblyOrder(@RequestBody UUID orderId);

    @PostMapping("/assembly/failed")
    ResponseEntity<OrderDto> assemblyOrderFailed(@RequestBody UUID orderId);
}
