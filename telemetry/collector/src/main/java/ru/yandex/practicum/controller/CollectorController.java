package ru.yandex.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.exception.NoHandlerException;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.handler.HubEventHandler;
import ru.yandex.practicum.handler.SensorEventHandler;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
@Slf4j
public class CollectorController extends CollectorControllerGrpc.CollectorControllerImplBase {
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlers;
    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;

    public CollectorController(Set<HubEventHandler> hubEventHandlers, Set<SensorEventHandler> sensorEventHandlers) {
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getMessageType, //ключ тип сообщения
                        Function.identity() //значение сам обработчик
                ));
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(
                        SensorEventHandler::getMessageType,
                        Function.identity()
                ));
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        sendEvent(request.getPayloadCase(),
                sensorEventHandlers,
                handler -> handler.handle(request),
                responseObserver,
                "SensorEvent");
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        sendEvent(request.getPayloadCase(),
                hubEventHandlers,
                handler -> handler.handle(request),
                responseObserver,
                "HubEvent");
    }

    private <C extends Enum<C>, H> void sendEvent(
            C eventType,
            Map<C, H> handlers,
            Consumer<H> handleAction,
            StreamObserver<Empty> responseObserver,
            String eventCategory
    ) {
        try {
            Optional.ofNullable(handlers.get(eventType))
                    .ifPresentOrElse(
                            handler -> {
                                log.trace("Обработка события обработчиком {}", handler.getClass().getSimpleName());
                                handleAction.accept(handler);
                                responseObserver.onNext(Empty.getDefaultInstance());
                                responseObserver.onCompleted();
                                log.debug("Успешная обработка события {}", eventType);
                            },
                            () -> {
                                String errorMessage = String.format("Не найден обработчик для типа {}", eventType);
                                log.warn(errorMessage);
                                throw new NoHandlerException(errorMessage);
                            }
                    );
        } catch (Exception e) {
            log.error("Ошибка в процессе обработки сообщения {}", eventType, e);

            Status status;
            if (e instanceof NoHandlerException) {
                status = Status.NOT_FOUND.withDescription(e.getMessage());
            } else {
                String errorDetails = String.format("Ошибка обработки события %s: %s",
                        eventType, e.getMessage());
                status = Status.INTERNAL.withDescription(errorDetails);
            }
            responseObserver.onError(new StatusRuntimeException(status));
        }
    }
}
