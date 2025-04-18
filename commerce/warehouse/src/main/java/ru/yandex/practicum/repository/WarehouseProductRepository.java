package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.WarehouseProduct;

import java.util.UUID;
@Repository
public interface WarehouseProductRepository extends JpaRepository<WarehouseProduct, UUID> {
}
