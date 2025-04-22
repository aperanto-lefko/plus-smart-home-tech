package ru.yandex.practicum.store.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.enums.ProductState;
import ru.yandex.practicum.store.enums.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {
    UUID productId;
    @NotBlank(message = "Наименование товара должно быть указано")
    String productName;
    @NotBlank(message = "Описание товара должно быть указано")
    String description;
    String imageSrc;
    @NotNull(message = "Поле \"Состояние остатка\" не должно быть пустым")
    QuantityState quantityState;
    @NotNull(message = "Поле \"Статус товара\" не должно быть пустым")
    ProductState productState;
    @NotNull(message = "Поле \"Категория товара\" не должно быть пустым")
    ProductCategory productCategory;
    @DecimalMin(value = "1.0", message = "Цена товара должна быть не меньше 1")
    BigDecimal price;
}
