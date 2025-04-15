package ru.yandex.practicum.store.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "shopping-store-service", path = "/api/v1/shopping-store", fallback = ShoppingStoreServiceFallback.class)
public interface ShoppingStoreServiceClient {

}
