package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.IncompleteProductListException;
import ru.yandex.practicum.exception.StoreServiceReturnedNullException;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.feign.ShoppingStoreServiceClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentServiceImpl implements PaymentService {
    ShoppingStoreServiceClient shoppingStoreServiceClient;

    @Override
    @Transactional
    public PaymentDto createPayment(OrderDto orderDto) {
        return null;
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        BigDecimal productPrice = orderDto.getProductPrice();
        BigDecimal taxRate = BigDecimal.valueOf(1.1);
        BigDecimal productPriceWithTax = productPrice
                .multiply(taxRate)
                .setScale(2, RoundingMode.HALF_UP);
        return productPriceWithTax.add(orderDto.getDeliveryPrice());
    }

    @Override
    public void refundPayment(UUID paymentId) {

    }

    @Override
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        Map<UUID, Integer> orderProducts = orderDto.getProducts();
        Set<UUID> productIds = orderDto.getProducts().keySet();
        List<ProductDto> listProducts = Optional.ofNullable(shoppingStoreServiceClient.getProductsByIds(productIds).getBody())
                .orElseThrow(() -> new StoreServiceReturnedNullException("Не удалось получить список товаров"));
        if (productIds.size() != listProducts.size()) {
            throw new IncompleteProductListException("Количество товаров не соответствует количеству запрошенных ids");
        }
        return listProducts.stream()
                .map(product -> {
                    int quantity = orderProducts.get(product.getProductId());
                    return product.getPrice().multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void failPayment(UUID paymentId) {

    }
}
