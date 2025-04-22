package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.enums.ProductState;
import ru.yandex.practicum.store.enums.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID productId;
    @Column(name = "product_name", nullable = false)
    String productName;
    @Column(name = "description", nullable = false)
    String description;
    @Column(name = "image_src")
    String imageSrc;
    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_state", nullable = false)
    QuantityState quantityState;
    @Enumerated(EnumType.STRING)
    @Column(name = "product_state", nullable = false)
    ProductState productState;
    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", nullable = false)
    ProductCategory productCategory;
    @Column(name = "price", nullable = false)
    BigDecimal price;
}
