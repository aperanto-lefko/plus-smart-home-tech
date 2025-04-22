package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.yandex.practicum.store.dto.PageableDto;
import org.springframework.data.domain.Pageable;

import java.util.List;


@Mapper(componentModel = "spring")
public interface PageableMapper {
    default Pageable toPageable(PageableDto pageableRequest) {
        if (pageableRequest.getSort() == null || pageableRequest.getSort().isEmpty()) {
            return PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        }
        List<Sort.Order> orders = pageableRequest.getSort().stream()
                .map(str -> {
                    String[] sorts = str.split(",");
                    return sorts.length == 2 && "desc".equalsIgnoreCase(sorts[1])
                            ? Sort.Order.desc(sorts[0])
                            : Sort.Order.asc(sorts[0]);
                })
                .toList();
        return PageRequest.of(
                pageableRequest.getPage(),
                pageableRequest.getSize(),
                Sort.by(orders));

    }

}
