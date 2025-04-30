package ru.yandex.practicum.delivery.feign;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeliveryFeignConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new DeliveryFeignErrorDecoder();
    }
}
