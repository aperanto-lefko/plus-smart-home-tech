package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "warehouse_product")
@Getter
@Setter
public class WarehouseProduct {
    @Id
    @Column(name = "id", columnDefinition = "UUID", nullable = false)
    private UUID id;  // ID приходит извне, генерация отключена
    @Column(name = "isFragile", nullable = false)
    private boolean isFragile;
    @Embedded
    private Dimension dimensions;
    @Column(name = "weight", nullable = false)
    private double weight;
}
