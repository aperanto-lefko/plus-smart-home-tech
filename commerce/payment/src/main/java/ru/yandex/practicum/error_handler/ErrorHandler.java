package ru.yandex.practicum.error_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.IncompleteProductListException;
import ru.yandex.practicum.exception.StoreServiceReturnedNullException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler extends BaseErrorHandler{
    @ExceptionHandler(StoreServiceReturnedNullException.class)
    public ResponseEntity<ErrorResponse> handleStoreServiceReturnedNullException(StoreServiceReturnedNullException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorUserMessage = "Сервис магазина вернул null значение";
        logging(errorUserMessage, ex);
        return ResponseEntity
                .status(status)
                .body(createErrorResponse(
                        status,
                        errorUserMessage,
                        ex
                ));
    }
    @ExceptionHandler(IncompleteProductListException.class)
    public ResponseEntity<ErrorResponse> handleIncompleteProductListException(IncompleteProductListException ex) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        String errorUserMessage = "Список товаров неполный";
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
