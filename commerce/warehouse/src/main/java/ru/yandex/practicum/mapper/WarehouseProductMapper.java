package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.warehouse.dto.WarehouseProductDto;

@Mapper(componentModel = "spring", uses = DimensionMapper.class)
public interface WarehouseProductMapper {
    @Mapping(target = "id", source = "productId")
    WarehouseProduct toEntity(WarehouseProductDto request);
}
