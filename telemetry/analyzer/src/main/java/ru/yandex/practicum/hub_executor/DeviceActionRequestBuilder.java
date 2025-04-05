package ru.yandex.practicum.hub_executor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.model.Action;

import com.google.protobuf.Timestamp;
import ru.yandex.practicum.model.ScenarioAction;

import java.time.Instant;
import java.util.Optional;

@Component
public class DeviceActionRequestBuilder implements RequestBuilder<DeviceActionRequest, Action> {
    @Override
    public DeviceActionRequest build(Action action, String hubId) {
        ScenarioAction scenarioAction = findScenarioActionForHub(action, hubId)
                .orElseThrow(() -> new IllegalStateException(
                        "Не найден ScenarioAction для hubId %s и actionId %d".formatted(hubId, action.getId())));
        return DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioAction.getScenario().getName())
                .setAction(buildActionProto(scenarioAction))
                .setTimestamp(getCurrentTimestamp())
                .build();
    }

    private Optional<ScenarioAction> findScenarioActionForHub(Action action, String hubId) {
        return action.getScenarioActions().stream()
                .filter(sa -> sa.getScenario().getHubId().equals(hubId))
                .findFirst();
    }

    private DeviceActionProto buildActionProto(ScenarioAction scenarioAction) {
        return DeviceActionProto.newBuilder()
                .setSensorId(scenarioAction.getSensor().getId())
                .setType(ActionTypeProto.valueOf(scenarioAction.getAction().getType().name()))
                .setValue(scenarioAction.getAction().getValue())
                .build();
    }

    private Timestamp getCurrentTimestamp() {
        Instant now = Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();
    }
}
