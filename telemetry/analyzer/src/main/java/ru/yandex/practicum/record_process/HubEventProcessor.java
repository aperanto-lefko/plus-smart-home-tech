package ru.yandex.practicum.record_process;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.service.ScenarioService;
import ru.yandex.practicum.service.SensorService;


@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HubEventProcessor implements RecordProcessor<HubEventAvro> {
    SensorService sensorService;
    ScenarioService scenarioService;

    @Override
    public void process(HubEventAvro event) {
        var eventPayload = event.getEvent();

        switch (eventPayload) {
            case DeviceAddedEventAvro deviceAddedEvent ->
                    sensorService.addSensor(deviceAddedEvent.getId(), event.getHubId());
            case DeviceRemovedEventAvro deviceRemovedEvent ->
                    sensorService.removeSensor(deviceRemovedEvent.getId(), event.getHubId());
            case ScenarioAddedEventAvro scenarioAddedEvent ->
                    scenarioService.addScenario(scenarioAddedEvent, event.getHubId());
            case ScenarioRemovedEventAvro scenarioRemovedEvent ->
                    scenarioService.removeScenario(scenarioRemovedEvent.getName(), event.getHubId());
            case null, default -> log.warn("Неизвестный тип для обработки: {}", event.getEvent().getClass().getName());
        }
    }
}
