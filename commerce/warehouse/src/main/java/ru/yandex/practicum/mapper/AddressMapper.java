package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.warehouse.dto.AddressDto;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressDto toDto(Address address);
}
