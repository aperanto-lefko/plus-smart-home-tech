package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.WarehouseItem;


@Repository
public interface WareHouseItemRepository extends JpaRepository<WarehouseItem, Long> {
}
