package ru.yandex.practicum.service;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.feign.ShoppingCartServiceClient;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.delivery.enums.DeliveryState;
import ru.yandex.practicum.delivery.feign.DeliveryServiceClient;
import ru.yandex.practicum.exception.DeliveryServiceReturnedNullException;
import ru.yandex.practicum.exception.PaymentServiceReturnedNullException;
import ru.yandex.practicum.exception.WarehouseServiceReturnedNullException;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.general_dto.AddressDto;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;
import ru.yandex.practicum.order.enums.OrderState;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.payment.feign.PaymentServiceClient;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.feign.WarehouseServiceClient;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    ShoppingCartServiceClient cartServiceClient;
    WarehouseServiceClient warehouseServiceClient;
    DeliveryServiceClient deliveryServiceClient;
    PaymentServiceClient paymentServiceClient;

    @Override
    public List<OrderDto> getClientOrders(String userName) {
        log.info("Поиск заказа для пользователя {}", userName);
        validateUser(userName);
        List<Order> list = orderRepository.findByUsername(userName);
        if (list == null || list.isEmpty()) {
            throw new NoOrderFoundException("Заказ для пользователя " + userName + "не найден");
        }
        return orderMapper.toDtoList(list);
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateNewOrderRequest request) {
        log.info("Создание нового заказа {}", request);
        log.info("Отправка корзины для проверки на склад");
        BookedProductsDto bookedProductsDto;
        try {
            bookedProductsDto = Optional.ofNullable(
                    warehouseServiceClient.checkShoppingCart(request.getShoppingCartDto()).getBody()
            ).orElseThrow(() -> new WarehouseServiceReturnedNullException("Не удалось получить информацию о забронированных товарах"));
        } catch (FeignException.FeignClientException.BadRequest ex) {
            log.error("Сервис склада вернул 400");
            throw new WarehouseServiceReturnedNullException("Ошибка при проверке корзины на складе");
        }
        Order order = Order.builder()
                .shoppingCartId(request.getShoppingCartDto().getShoppingCartId())
                .state(OrderState.NEW)
                .products(request.getShoppingCartDto().getProducts())
                .deliveryWeight(bookedProductsDto.getDeliveryWeight())
                .deliveryVolume(bookedProductsDto.getDeliveryVolume())
                .fragile(bookedProductsDto.isFragile())
                .build();

        orderRepository.save(order);
        log.info("Запрос на склад для получения адреса");
        AddressDto warehouseAddressDto = warehouseServiceClient.getAddress().getBody();
        DeliveryDto deliveryDto = DeliveryDto.builder()
                .fromAddress(warehouseAddressDto)
                .toAddress(request.getAddressDto())
                .orderId(order.getOrderId())
                .deliveryState(DeliveryState.CREATED)
                .build();
        log.info("Запрос в сервис доставки для создания новой доставки {}", deliveryDto);
        DeliveryDto savedDeliveryDto = Optional.ofNullable(deliveryServiceClient.createDelivery(deliveryDto).getBody())
                .orElseThrow(()->new DeliveryServiceReturnedNullException("Доставку не удалось оформить"));

        log.info("Доставка успешно создана с id {}", savedDeliveryDto.getDeliveryId());
        order.setDeliveryId(savedDeliveryDto.getDeliveryId());
        log.info("Отправка в сервис доставки для расчета стоимости доставки для заказа id {}", order.getOrderId());
        BigDecimal deliveryPrice = Optional.ofNullable(deliveryServiceClient.calculateTotalCostDelivery(orderMapper.toDto(order)).getBody())
                .orElseThrow(() -> new DeliveryServiceReturnedNullException("Не удалось получить стоимость доставки"));
        order.setDeliveryPrice(deliveryPrice);
        log.info("Отправка в сервис платежей на расчет стоимости товаров");
        BigDecimal productsPrice = Optional.ofNullable(paymentServiceClient.calculateProductCost(orderMapper.toDto(order)).getBody())
                .orElseThrow(() -> new PaymentServiceReturnedNullException("Не удалось получить стоимость товаров"));
        order.setProductPrice(productsPrice);
        log.info("Отправка в сервис платежей на создание платежа");
        PaymentDto paymentDto = Optional.ofNullable(paymentServiceClient.createPayment(orderMapper.toDto(order)).getBody())
                .orElseThrow(() -> new PaymentServiceReturnedNullException("Не удалось получить платежный документ по заказу"));
        order.setTotalPrice(paymentDto.getTotalPayment());
        order.setPaymentId(paymentDto.getPaymentId());
        orderRepository.save(order);
        log.info("Заказ успешно создан {}", order.getOrderId());
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto returnOrder(ProductReturnRequest request) {
        log.info("Оформление возврата заказа {}", request);
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден id" + request.getOrderId()));
        log.info("Отправка товаров на склад для переучета");
        warehouseServiceClient.returnProductToWarehouse(request.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);
        log.info("Заказ {} успешно обработан для возврата", request);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto paymentOrder(UUID orderId) {

        return null;
    }

    @Override
    public OrderDto paymentOrderFailed(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto deliveryOrder(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto deliveryOrderFailed(UUID orderId) {
        return null;
    }

    @Override
    @Transactional
    public OrderDto completeOrder(UUID orderId) {
        return null;
    }

    @Override
    @Transactional
    public OrderDto calculateTotalOrderCost(UUID orderId) {
        return null;
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryOrderCost(UUID orderId) {
        return null;
    }

    @Override
    @Transactional
    public OrderDto assemblyOrder(UUID orderId) {
        return null;
    }

    @Override
    @Transactional
    public OrderDto assemblyOrderFailed(UUID orderId) {
        return null;
    }

    private void validateUser(String userName) {
        log.info("Проверка имени пользователя {}", userName);
        if (userName == null || userName.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }
    }
}
