package ru.yandex.practicum.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShippedToDeliveryRequest {
    @NotNull(message = "id заказа должен быть указан")
    UUID orderId;
    @NotNull(message = "id доставки должен быть указан")
    UUID deliveryId;
}
