package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Setter;

@Entity
@Table(name = "address")
@Setter
@Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "country", nullable = false)
    private String country;
    @Column(name = "city", nullable = false)
    private String city;
    @Column(name = "street", nullable = false)
    private String street;
    @Column(name = "house", nullable = false)
    private String house;
    @Column(name = "flat")
    private String flat;

    public static Address fromString(String addressValue) {
        return Address.builder()
                .country(addressValue)
                .city(addressValue)
                .street(addressValue)
                .house(addressValue)
                .flat(addressValue)
                .build();
    }
}
