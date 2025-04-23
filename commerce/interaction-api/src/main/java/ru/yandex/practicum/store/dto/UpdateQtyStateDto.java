package ru.yandex.practicum.store.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.store.enums.QuantityState;

import java.util.UUID;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateQtyStateDto {
    @NotNull(message = "id продукта должен быть указан")
    UUID productId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    QuantityState quantityState;
}
