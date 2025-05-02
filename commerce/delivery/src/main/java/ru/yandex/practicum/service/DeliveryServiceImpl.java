package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.delivery.enums.DeliveryState;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.feign.OrderServiceClient;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.warehouse.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.warehouse.feign.WarehouseServiceClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryServiceImpl implements DeliveryService {
    OrderServiceClient orderServiceClient;
    WarehouseServiceClient warehouseServiceClient;
    DeliveryRepository deliveryRepository;
    DeliveryMapper deliveryMapper;
    BigDecimal BASE_RATE = BigDecimal.valueOf(5.0);

    @Override
    @Transactional
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        log.info("Создание новой доставки {}", deliveryDto);
        Delivery delivery = deliveryRepository.save(deliveryMapper.toEntity(deliveryDto));
        log.info("Доставка успешно сохранена с id {}", delivery.getDeliveryId());
        return deliveryMapper.toDto(delivery);
    }

    @Override
    @Transactional
    public void completeDelivery(UUID orderId) {
        log.info("Подтверждение успешной доставки для orderId {}", orderId);
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        log.info("Вызов сервиса заказов для оформления успешной доставки {}", delivery);
        orderServiceClient.deliveryOrder(orderId);
        log.info("Сохранение доставки в базу данных {}", delivery);
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public void pickupOrderForDelivery(UUID orderId) {
        log.info("Оформление заказа {} в доставку", orderId);
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        log.info("Вызов сервиса склада для оформления доставки {}", delivery);
        warehouseServiceClient.sendProductsToDelivery(ShippedToDeliveryRequest.builder()
                .orderId(orderId)
                .deliveryId(delivery.getDeliveryId())
                .build());
        log.info("Сохранение доставки в базу данных {}", delivery);
        deliveryRepository.save(delivery);
    }


    @Override
    public void failDelivery(UUID orderId) {
        log.info("Оформление неудачной доставки для заказа {}", orderId);
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        log.info("Вызов сервиса заказов для оформления неудачной доставки {}", delivery);
        orderServiceClient.deliveryOrderFailed(orderId);
        log.info("Сохранение доставки в базу данных {}", delivery);
        deliveryRepository.save(delivery);
    }

    @Override
    public BigDecimal calculateTotalCostDelivery(OrderDto orderDto) {
        UUID deliveryId = orderDto.getDeliveryId();
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка с id " + deliveryId + " не найдена"));
        Address fromAddress = delivery.getFromAddress();
        Address toAddress = delivery.getToAddress();
        BigDecimal cost = BASE_RATE;
        if (fromAddress.getStreet().contains("ADDRESS_1")) {
            cost.multiply(BigDecimal.valueOf(1));
        } else if (fromAddress.getStreet().contains("ADDRESS_2")) {
            cost.multiply(BigDecimal.valueOf(2)).add(BASE_RATE);
        }
        if (orderDto.isFragile()) {
            BigDecimal fragileFee = cost.multiply(BigDecimal.valueOf(0.2));
            cost.add(fragileFee);
        }
        // Учитываем вес (×0.3)
        BigDecimal weightFee = BigDecimal.valueOf(orderDto.getDeliveryWeight()).multiply(BigDecimal.valueOf(0.3));
        cost = cost.add(weightFee);

        // Учитываем объём (×0.2)
        BigDecimal volumeFee = BigDecimal.valueOf(orderDto.getDeliveryVolume()).multiply(BigDecimal.valueOf(0.2));
        cost = cost.add(volumeFee);

        // Учитываем адрес доставки (если улицы не совпадают)
        if (!fromAddress.getStreet().equalsIgnoreCase(toAddress.getStreet())) {
            BigDecimal addressMismatchFee = cost.multiply(BigDecimal.valueOf(0.2));
            cost = cost.add(addressMismatchFee);
        }

        return cost.setScale(2, RoundingMode.HALF_UP); // округление до копеек

    }

    private Delivery getDeliveryByOrderId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка с orderId " + orderId + " не найдена"));
    }
}
