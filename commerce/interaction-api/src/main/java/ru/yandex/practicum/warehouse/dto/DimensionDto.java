package ru.yandex.practicum.warehouse.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DimensionDto {
    @DecimalMin(value = "1.0", message = "Размер ширины должен быть больше 1")
    double width;
    @DecimalMin(value = "1.0", message = "Размер высоты должен быть больше 1")
    double height;
    @DecimalMin(value = "1.0", message = "Размер глубины должен быть больше 1")
    double depth;
}
