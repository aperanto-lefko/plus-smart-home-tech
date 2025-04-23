package ru.yandex.practicum.config;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "cart.process")
@Getter
@Setter // Lombok
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartProcessingProperties {
    @Min(1)
    int maxAttempts;
}
