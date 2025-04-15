package ru.yandex.practicum.error_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.ProductNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String errorUserMessage = "Продукт не найден";
        log.error(errorUserMessage);
        return ResponseEntity
                .status(status)
                .body(createErrorResponse(
                        status,
                        errorUserMessage,
                        ex
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorUserMessage = "Внутренняя ошибка сервера";
        log.error(errorUserMessage, ex);
        return ResponseEntity
                .status(status)
                .body(createErrorResponse(
                        status,
                        errorUserMessage,
                        ex
                ));
    }

    private ErrorResponse createErrorResponse(HttpStatus status,
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

    private List<StackTraceElement> getSafeStackTrace(Throwable ex) {
        return ex.getStackTrace() != null ?
                Arrays.asList(ex.getStackTrace()) :
                Collections.emptyList();
    }

    private List<Throwable> getSafeSuppressed(Throwable ex) {
        return ex.getSuppressed() != null && ex.getSuppressed().length > 0 ?
                Arrays.asList(ex.getSuppressed()) :
                null;
    }
}
