package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.general_dto.AddressDto;
import ru.yandex.practicum.model.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressDto toDto(Address address);

    Address toEntity(AddressDto addressDto);
}
