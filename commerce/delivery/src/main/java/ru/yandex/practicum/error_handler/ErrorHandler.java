package ru.yandex.practicum.error_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.NoDeliveryFoundException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler extends BaseErrorHandler{
    @ExceptionHandler(NoDeliveryFoundException.class)
    public ResponseEntity<ErrorResponse> handleDeliveryNotFoundException(NoDeliveryFoundException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorUserMessage = "Сервис доставки вернул null значение";
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
