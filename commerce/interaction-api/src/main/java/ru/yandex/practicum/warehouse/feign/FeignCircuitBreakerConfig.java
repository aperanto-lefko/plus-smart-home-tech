package ru.yandex.practicum.warehouse.feign;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignCircuitBreakerConfig {
    @Bean
    public CircuitBreaker feignCircuitBreaker() {
        return CircuitBreaker.ofDefaults("warehouseService");
    }
}
