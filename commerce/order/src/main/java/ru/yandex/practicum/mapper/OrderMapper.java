package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.order.dto.OrderDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
    List<OrderDto> ToDtoList(List<Order> list);
}
