package ru.yandex.practicum.order.feign;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderFeignConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new OrderFeignErrorDecoder();
    }
}
