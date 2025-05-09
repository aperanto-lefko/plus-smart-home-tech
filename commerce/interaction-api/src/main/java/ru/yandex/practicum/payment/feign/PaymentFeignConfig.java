package ru.yandex.practicum.payment.feign;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PaymentFeignConfig {
    @Bean(name = "paymentErrorDecoder")
    public ErrorDecoder paymentErrorDecoder() {
        return new PaymentFeignErrorDecoder();
    }
}
