package ru.yandex.practicum.delivery.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.delivery.enums.DeliveryState;
import ru.yandex.practicum.general_dto.AddressDto;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryDto {
    UUID deliveryId;
    @NotNull(message = "Адрес исходного пункта должен быть указан")
    @Valid
    AddressDto fromAddress;
    @NotNull(message = "Адрес пункта назначения должен быть указан")
    @Valid
    AddressDto toAddress;
    @NotNull(message = "id заказа должен быть указан")
    UUID orderId;
    @NotNull(message = "Статус доставки должен быть указан")
    DeliveryState deliveryState;
}
