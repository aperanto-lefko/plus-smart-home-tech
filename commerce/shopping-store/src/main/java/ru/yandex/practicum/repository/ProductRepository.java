package ru.yandex.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.enums.ProductState;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByProductCategory(ProductCategory productCategory, Pageable pageable);
}

