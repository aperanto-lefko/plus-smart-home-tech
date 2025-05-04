package ru.yandex.practicum.order.dto;

import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "id заказа должен быть указан")
    UUID orderId;
    @NotNull(message = "id корзины должен быть указан")
    UUID shoppingCartId;
    @NotNull(message = "id платежа должен быть указан")
    UUID paymentId;
    @NotNull(message = "id доставки должен быть указан")
    UUID deliveryId;
    @NotNull(message = "Список товаров должен быть указан")
    Map<UUID, Integer> products;
    @NotNull(message = "Статус заказа должен быть указан")
    OrderState state;
    @NotNull(message = "Общий вес доставки должен быть указан")
    double deliveryWeight;
    @NotNull(message = "Общий объем доставки должен быть указан")
    double deliveryVolume;
    @NotNull(message = "Признак хрупкости товара должен быть указан")
    boolean fragile;
    BigDecimal totalPrice;
    BigDecimal  deliveryPrice;
    BigDecimal  productPrice;
}
