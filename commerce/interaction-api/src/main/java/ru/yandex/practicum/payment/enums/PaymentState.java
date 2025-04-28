package ru.yandex.practicum.payment.enums;

public enum PaymentState {
    PENDING, //ожидает оплаты
    SUCCESS, // успешно оплачен
    FAILED //ошибка в процессе оплаты
}
