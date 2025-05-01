package ru.yandex.practicum.order.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.error_handler.ErrorResponse;
import ru.yandex.practicum.exception.OrderServiceException;


@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderFeignErrorDecoder implements ErrorDecoder {
    ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            String body = Util.toString(response.body().asReader());
            ErrorResponse error = objectMapper.readValue(body, ErrorResponse.class);
            return switch (response.status()) {
                case 400 -> new OrderServiceException("Сервис заказа вернул 400 " + error.getUserMessage());
                case 503 -> new OrderServiceException("Сервис заказа вернул 503. Сервис недоступен: " + error.getUserMessage());
                default -> new RuntimeException("Неизвестная ошибка при вызове Feign: " + error.getMessage());
            };
        } catch (Exception e) {
            return new RuntimeException("Feign вызов завершился ошибкой (невозможно прочитать тело): " + e.getMessage(), e);
        }
    }
}
