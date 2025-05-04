package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.payment.dto.PaymentDto;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentDto toDto (Payment payment);
    Payment toEntity (PaymentDto paymentDto);
}
