package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.util.UUID;

@Entity
@Table(name = "warehouse_product")
@Getter
public class WarehouseProduct {
    @Id
    @Column(name = "id", columnDefinition = "UUID", nullable = false)
    private UUID id;  // ID приходит извне, генерация отключена
    @Column(name = "fragile", nullable = false)
    private boolean fragile;
    @Embedded
    private Dimension dimension;
    @Column(name = "weight", nullable = false)
    private double weight;
}
