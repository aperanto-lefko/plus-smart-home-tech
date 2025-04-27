package ru.yandex.practicum.order.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.order.enums.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDto {
    UUID orderId;
    UUID shoppingCartId;
    UUID paymentId;
    UUID deliveryId;
    Map<UUID, Integer> products;
    OrderState state;
    double deliveryWeight;
    double deliveryVolume;
    boolean fragile;
    BigDecimal totalPrice;
    BigDecimal  deliveryPrice;
    BigDecimal  productPrice;
}
