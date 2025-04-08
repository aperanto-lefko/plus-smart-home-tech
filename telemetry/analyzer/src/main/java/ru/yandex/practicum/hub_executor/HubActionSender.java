package ru.yandex.practicum.hub_executor;

import com.google.protobuf.Empty;
import io.grpc.StatusRuntimeException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.ActionProcessingException;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.model.Action;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HubActionSender implements HubSender<Action, ActionProcessingException> {

    HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;
    RequestBuilder<DeviceActionRequest, Action> requestBuilder;

    @Override
    public void send(Action action, String hubId) throws ActionProcessingException {
        DeviceActionRequest request = requestBuilder.build(action, hubId);
        sendRequest(request, hubId, action);
    }

    private void sendRequest(DeviceActionRequest request, String hubId, Action action) {
        try {
            Empty response = hubRouterClient.handleDeviceAction(request);
            if (response != null) {
                log.info("Действие action {} успешно отправлено на hubId {}", action, hubId);
            } else {
                log.warn("В результате отправки action {} на hubId {} пришел ответ null", action, hubId);
            }
        } catch (StatusRuntimeException e) {
            log.error("Отправка действия action {} на hubId {} завершилась неудачей", action, hubId);
            throw new ActionProcessingException(
                    "Отправка действия action " + action + "на hubId " + hubId + " завершилась неудачей", e);
        }
    }

}
