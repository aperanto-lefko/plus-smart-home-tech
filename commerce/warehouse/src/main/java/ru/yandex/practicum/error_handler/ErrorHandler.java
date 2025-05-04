package ru.yandex.practicum.error_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.OrderBookingNoFoundException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.exception.WarehouseProductNotFoundException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler extends BaseErrorHandler {
    @ExceptionHandler({SpecifiedProductAlreadyInWarehouseException.class,
            WarehouseProductNotFoundException.class,
            ProductInShoppingCartLowQuantityInWarehouse.class,
            OrderBookingNoFoundException.class})
    public ResponseEntity<ErrorResponse> handleServiceLayerExceptions(RuntimeException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorUserMessage = getUserFriendlyMessage(ex);
        logging(errorUserMessage, ex);
        return ResponseEntity
                .status(status)
                .body(createErrorResponse(
                        status,
                        errorUserMessage,
                        ex
                ));
    }


    private String getUserFriendlyMessage(Exception ex) {
        return switch (ex) {
            case SpecifiedProductAlreadyInWarehouseException specifiedProductAlreadyInWarehouseException ->
                    "Товар уже существует на складе";
            case WarehouseProductNotFoundException warehouseProductNotFoundException -> "Товар отсутствует на складе";
            case ProductInShoppingCartLowQuantityInWarehouse productInShoppingCartLowQuantityInWarehouse ->
                    "Товара не хватает на складе";
            case OrderBookingNoFoundException orderBookingNoFoundException ->
                    "Запись orderBooking  отсутствует на складе";
            case null, default -> "Произошла непредвиденная ошибка";
        };
    }
}
