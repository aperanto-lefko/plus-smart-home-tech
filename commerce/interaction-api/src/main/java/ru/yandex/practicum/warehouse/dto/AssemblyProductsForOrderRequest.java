package ru.yandex.practicum.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssemblyProductsForOrderRequest {
    @NotNull(message = "Список товаров должен быть указан")
    Map<UUID, Integer> products;
    @NotNull(message = "id заказа должен быть указан")
    UUID orderId;
}
