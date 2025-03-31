package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "ru.yandex.practicum")
public class AggregatorApp {
    public static void main(String[] args) {
//        ConfigurableApplicationContext context = SpringApplication.run(AggregatorApp.class, args);
//        AggregationStarter aggregator = context.getBean(AggregationStarter.class);
//        aggregator.start();
        SpringApplication.run(AggregatorApp.class, args);
    }
}