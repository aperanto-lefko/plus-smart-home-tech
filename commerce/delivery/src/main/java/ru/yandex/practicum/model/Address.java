package ru.yandex.practicum.model;

import jakarta.persistence.Column;
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

import java.util.UUID;
@Data
@Table(name = "addresses")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "address_id")
    UUID addressId;
    @Column(name = "country", nullable = false)
    String country;
    @Column(name = "city", nullable = false)
    String city;
    @Column(name = "street", nullable = false)
    String street;
    @Column(name = "house", nullable = false)
    String house;
    @Column(name = "flat", nullable = false)
    String flat;
}
