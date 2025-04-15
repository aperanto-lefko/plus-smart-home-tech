package ru.yandex.practicum.store.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageableRequest {
    @NotNull(message = "Номер страницы должен быть указан")
    @Min(value= 0, message = "Минимальное значение page должно быть 0")
    int page;
    @NotNull(message = "Количество элементов на странице должен быть указан")
    @Min(value= 1, message = "Минимальное значение page должно быть 0")
    int size;
    List<String> sort;
}
