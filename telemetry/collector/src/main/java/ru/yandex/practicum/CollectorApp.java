package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication (scanBasePackages = {
        "ru.yandex.practicum.collector",
        "ru.yandex.practicum.kafka-commons"
    })

public class CollectorApp {
    public static void main(String[] args) {
        SpringApplication.run(CollectorApp.class, args);
    }
}
