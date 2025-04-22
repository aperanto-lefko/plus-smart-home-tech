package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.WarehouseItem;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Repository
public interface WarehouseItemRepository extends JpaRepository<WarehouseItem, Long> {
    List<WarehouseItem> findAllByProduct_IdIn(Set<UUID> productIds);
    Optional<WarehouseItem>findByProduct_Id(UUID productId);
}
