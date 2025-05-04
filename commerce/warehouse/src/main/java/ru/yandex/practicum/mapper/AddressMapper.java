package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.general_dto.AddressDto;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    @Mapping(target = "country", source = "country")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "street", source = "street")
    @Mapping(target = "house", source = "house")
    @Mapping(target = "flat", source = "flat")
    AddressDto toDto(Address address);
}
