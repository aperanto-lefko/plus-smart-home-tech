package ru.yandex.practicum.delivery.feign;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeliveryFeignConfig {
    @Bean(name = "deliveryErrorDecoder")
    public ErrorDecoder deliveryErrorDecoder() {
        return new DeliveryFeignErrorDecoder();
    }
}
