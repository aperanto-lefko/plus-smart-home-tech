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
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.service.WarehouseService;
import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.general_dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.warehouse.dto.WarehouseProductDto;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseController {
    final WarehouseService warehouseService;

    @PutMapping
    public ResponseEntity<Void> addNewProduct(@RequestBody @Valid WarehouseProductDto newProduct) {
        warehouseService.createProduct(newProduct);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
    @PostMapping("/check")
    public ResponseEntity<BookedProductsDto> checkShoppingCart(@RequestBody ShoppingCartDto cart) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(warehouseService.checkShoppingCart(cart));
    }
    @PostMapping("/add")
    public ResponseEntity<Void> addAndChangeQuantityProduct(@RequestBody @Valid AddProductToWarehouseRequest productRequest) {
        warehouseService.addAndChangeQuantityProduct(productRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
    @GetMapping("/address")
    public ResponseEntity<AddressDto> getAddress() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(warehouseService.getAddress());
    }
    @PostMapping("/assembly")
    public ResponseEntity<BookedProductsDto> prepareOrderItemsForShipment(@RequestBody @Valid AssemblyProductsForOrderRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(warehouseService.prepareOrderItemsForShipment(request));
    }
    @PostMapping("/return")
    public ResponseEntity<Void> returnProductToWarehouse(@RequestBody Map <UUID, Integer> products) {
        warehouseService.returnProductToWarehouse(products);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
//    @PostMapping("/shipped")
//    public ResponseEntity<Void> sendProductsToDelivery(@RequestBody @Valid ShippedToDeliveryRequest request) {
//        warehouseService.sendProductsToDelivery(request);
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .build();
//    }
}
