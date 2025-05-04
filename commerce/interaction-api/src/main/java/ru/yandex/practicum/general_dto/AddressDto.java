package ru.yandex.practicum.general_dto;

import jakarta.validation.constraints.NotBlank;
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
public class AddressDto {
    @NotBlank(message = "Страна должна быть указана")
    String country;
    @NotBlank(message = "Город должен быть указан")
    String city;
    @NotBlank(message = "Улица должна быть указана")
    String street;
    @NotBlank(message = "Дом должен быть указан")
    String house;
    @NotBlank(message = "Квартира должна быть указана")
    String flat;
}
