package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryServiceImpl implements DeliveryService {
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
    public void completeDelivery(UUID orderId) {

    }

    @Override
    public void pickupOrderForDelivery(UUID orderId) {
    }

    @Override
    public void failDelivery(UUID orderId) {

    }

    @Override
    public BigDecimal calculateTotalCostDelivery(OrderDto orderDto) {
        UUID deliveryId = orderDto.getDeliveryId();
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(()-> new NoDeliveryFoundException("Доставка с id " + deliveryId + " не найдена"));
        Address fromAddress = delivery.getFromAddress();
        Address toAddress = delivery.getToAddress();
        BigDecimal cost = BASE_RATE;
        if(fromAddress.getStreet().contains("ADDRESS_1")) {
            cost.multiply(BigDecimal.valueOf(1));
        } else if (fromAddress.getStreet().contains("ADDRESS_2")) {
            cost.multiply(BigDecimal.valueOf(2)).add(BASE_RATE);
        }
        if(orderDto.isFragile()) {
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
}
