package ru.yandex.practicum.error_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.exception.WarehouseProductNotFoundException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler extends  BaseErrorHandler{
    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    public ResponseEntity<ErrorResponse> handleProductAlreadyInWarehouseException(SpecifiedProductAlreadyInWarehouseException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorUserMessage = "Товар уже существует на складе";
        logging(errorUserMessage, ex);
        return ResponseEntity
                .status(status)
                .body(createErrorResponse(
                        status,
                        errorUserMessage,
                        ex
                ));
    }
    @ExceptionHandler(WarehouseProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(WarehouseProductNotFoundException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorUserMessage = "Товар отсутствует на складе";
        logging(errorUserMessage, ex);
        return ResponseEntity
                .status(status)
                .body(createErrorResponse(
                        status,
                        errorUserMessage,
                        ex
                ));
    }

    @ExceptionHandler( ProductInShoppingCartLowQuantityInWarehouse.class)
    public ResponseEntity<ErrorResponse> handleProductLowQuantityException( ProductInShoppingCartLowQuantityInWarehouse ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorUserMessage = "Товара не хватает на складе";
        logging(errorUserMessage, ex);
        return ResponseEntity
                .status(status)
                .body(createErrorResponse(
                        status,
                        errorUserMessage,
                        ex
                ));
    }
}
