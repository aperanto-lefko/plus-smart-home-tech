package ru.yandex.practicum.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.channelfactory.GrpcChannelFactory;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Configuration
public class GrpcConfig {

    @Value("${grpc.client.hub-router.address}")
    private String address;

    @Bean
    public ManagedChannel grpcChannel() {
        return ManagedChannelBuilder.forTarget(address)
                .usePlaintext()
                .build();
    }

    @Bean
    public HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub(ManagedChannel channel) {
        return HubRouterControllerGrpc.newBlockingStub(channel);
    }
}

