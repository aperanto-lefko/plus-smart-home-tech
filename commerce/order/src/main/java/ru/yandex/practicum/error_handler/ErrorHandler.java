package ru.yandex.practicum.error_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.NoOrderFoundException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler extends BaseErrorHandler{
    @ExceptionHandler(NoOrderFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(NoOrderFoundException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorUserMessage = "Заказ для пользователя не найден";
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
