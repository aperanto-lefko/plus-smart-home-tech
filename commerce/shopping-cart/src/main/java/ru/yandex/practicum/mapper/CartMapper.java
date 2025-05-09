package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;

@Mapper(componentModel = "spring")
public interface CartMapper {
    ShoppingCartDto toDto(ShoppingCart cart);
    ShoppingCart toEntity(ShoppingCartDto cart);
}
