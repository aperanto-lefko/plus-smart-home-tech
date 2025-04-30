package ru.yandex.practicum.error_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.DeliveryServiceException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.PaymentServiceException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class BaseErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class,})
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorUserMessage = "Запрос составлен некорректно";
        logging(errorUserMessage, ex);
        return ResponseEntity
                .status(status)
                .body(createErrorResponse(
                        status,
                        errorUserMessage,
                        ex
                ));
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorUserMessage = "Внутренняя ошибка сервера";
        logging(errorUserMessage, ex);
        return ResponseEntity
                .status(status)
                .body(createErrorResponse(
                        status,
                        errorUserMessage,
                        ex
                ));
    }

    @ExceptionHandler({NotAuthorizedUserException.class,
            DeliveryServiceException.class,
            PaymentServiceException.class})
    public ResponseEntity<ErrorResponse> handleNotAuthorizedException(RuntimeException ex) {
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
            case NotAuthorizedUserException notAuthorizedUserException ->
                    "Пользователь не авторизован, поле имя некорректно";
            case DeliveryServiceException deliveryServiceException -> "Ошибка при работе с сервисом склада";
            case PaymentServiceException paymentServiceException -> "Ошибка при работе с сервисом оплаты";
            case null, default -> "Произошла непредвиденная ошибка";
        };
    }

    protected ErrorResponse createErrorResponse(HttpStatus status,
                                                String message,
                                                Throwable ex) {
        return new ErrorResponse(
                ex.getCause(),
                getSafeStackTrace(ex),
                status.name(),
                message,
                ex.getMessage(),
                getSafeSuppressed(ex),
                ex.getLocalizedMessage());
    }

    protected List<StackTraceElement> getSafeStackTrace(Throwable ex) {
        return ex.getStackTrace() != null ?
                Arrays.asList(ex.getStackTrace()) :
                Collections.emptyList();
    }

    protected List<Throwable> getSafeSuppressed(Throwable ex) {
        return ex.getSuppressed() != null && ex.getSuppressed().length > 0 ?
                Arrays.asList(ex.getSuppressed()) :
                null;
    }

    protected void logging(String message, Throwable ex) {
        log.error(message, ex.getMessage(), ex);
    }
}
