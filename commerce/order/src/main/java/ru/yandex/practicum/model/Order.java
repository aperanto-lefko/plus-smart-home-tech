package ru.yandex.practicum.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.order.enums.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    UUID orderId;
    @Column(name = "shoppingCart_id", nullable = false)
    UUID shoppingCartId;
    @Column(name = "payment_id", nullable = false)
    UUID paymentId;
    @Column(name = "delivery_id", nullable = false)
    UUID deliveryId;
    @Column(name = "user_name",nullable = false)
    String username;
    @ElementCollection
    @CollectionTable(name = "order_products", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    Map<UUID, Integer> products;
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    OrderState state;
    @Column(name = "delivery_weight")
    double deliveryWeight; //вес товаров
    @Column(name = "delivery_volume")
    double deliveryVolume; //объём товаров
    @Column(name = "fragile")
    boolean fragile;
    @Column(name = "total_price")
    BigDecimal totalPrice; //итоговая цена
    @Column(name = "delivery_price")
    BigDecimal deliveryPrice; //цена доставки
    @Column(name = "product_price")
    BigDecimal productPrice; //цена всех товаров
}
