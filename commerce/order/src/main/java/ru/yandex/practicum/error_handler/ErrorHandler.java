package ru.yandex.practicum.error_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.DeliveryServiceReturnedNullException;
import ru.yandex.practicum.exception.PaymentServiceReturnedNullException;
import ru.yandex.practicum.exception.WarehouseServiceReturnedNullException;
import ru.yandex.practicum.exception.NoOrderFoundException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler extends BaseErrorHandler{
    @ExceptionHandler({
            NoOrderFoundException.class,
            WarehouseServiceReturnedNullException.class,
            DeliveryServiceReturnedNullException.class,
            PaymentServiceReturnedNullException.class
    })
    public ResponseEntity<ErrorResponse> handleCustomExceptions(RuntimeException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorUserMessage = getUserFriendlyMessage(ex);
        logging(errorUserMessage, ex);

        return ResponseEntity
                .status(status)
                .body(createErrorResponse(status, errorUserMessage, ex));
    }

    private String getUserFriendlyMessage(Exception ex) {
        return switch (ex) {
            case NoOrderFoundException noOrderFoundException -> "Заказ для пользователя не найден";
            case WarehouseServiceReturnedNullException warehouseServiceReturnedNullException ->
                    "Сервис склада вернул null значение";
            case DeliveryServiceReturnedNullException deliveryServiceReturnedNullException ->
                    "Сервис доставки вернул null значение";
            case PaymentServiceReturnedNullException paymentServiceReturnedNullException ->
                "Сервис расчета платежей вернул null значение";
            case null, default -> "Произошла непредвиденная ошибка";
        };
    }
}
