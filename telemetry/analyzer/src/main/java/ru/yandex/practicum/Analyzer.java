package ru.yandex.practicum;

import net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(GrpcClientAutoConfiguration.class)
@SpringBootApplication(scanBasePackages = "ru.yandex.practicum")
public class Analyzer {
    public static void main(String[] args) {
        SpringApplication.run(Analyzer.class, args);
    }
}