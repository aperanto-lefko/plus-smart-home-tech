package ru.yandex.practicum.store.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RemoveProductDto {
    @NotNull(message = "id продукта должен быть указан")
    UUID productId;

    @JsonCreator
    public RemoveProductDto(String productIdStr) {
        this.productId = UUID.fromString(productIdStr);
    }
}
