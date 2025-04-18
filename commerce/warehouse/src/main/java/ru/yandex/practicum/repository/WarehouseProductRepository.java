package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.WarehouseProduct;

import java.util.List;
import java.util.UUID;
@Repository
public interface WarehouseProductRepository extends JpaRepository<WarehouseProduct, UUID> {

}
