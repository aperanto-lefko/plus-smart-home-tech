package ru.yandex.practicum.error_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.CartNotFoundException;
import ru.yandex.practicum.exception.NoProductInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.WarehouseServiceException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler extends BaseErrorHandler{

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCartNotFoundException(CartNotFoundException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorUserMessage = "Пользователь не авторизован, поле имя некорректно";
        logging(errorUserMessage, ex);
        return ResponseEntity
                .status(status)
                .body(createErrorResponse(
                        status,
                        errorUserMessage,
                        ex
                ));
    }
    @ExceptionHandler(NoProductInShoppingCartException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundInCartException(NoProductInShoppingCartException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorUserMessage = "Продукты в корзине не обнаружены";
        logging(errorUserMessage, ex);
        return ResponseEntity
                .status(status)
                .body(createErrorResponse(
                        status,
                        errorUserMessage,
                        ex
                ));
    }
    @ExceptionHandler(WarehouseServiceException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundInCartException(WarehouseServiceException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorUserMessage = "Товары не найдены на складе";
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
