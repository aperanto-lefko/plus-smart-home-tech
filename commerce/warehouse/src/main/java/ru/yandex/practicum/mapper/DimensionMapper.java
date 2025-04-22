package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.model.Dimension;
import ru.yandex.practicum.warehouse.dto.DimensionDto;

@Mapper(componentModel = "spring")
public interface DimensionMapper {
    Dimension toEntity(DimensionDto dto);
   }
