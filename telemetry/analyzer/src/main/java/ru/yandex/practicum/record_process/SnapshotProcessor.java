package ru.yandex.practicum.record_process;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.ActionProcessingException;
import ru.yandex.practicum.hub_executor.HubActionSender;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorEventAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.ConditionOperationType;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.service.ScenarioService;


import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SnapshotProcessor implements RecordProcessor<SensorsSnapshotAvro> {
    HubActionSender hubActionSender;
    ScenarioService scenarioService;

    @Override
    public void process(SensorsSnapshotAvro snapshot) {
       try {
           String hubId = snapshot.getHubId();
           List<Scenario> scenarios = scenarioService.getScenariosByHubId(hubId);
           scenarios.stream()
                   .filter(scenario -> isScenarioMatch(scenario, snapshot))
                   .forEach(scenario -> sendAction(scenario, hubId));
       } catch (ActionProcessingException e) {
           log.warn("отправка сообщения на hub не удалась {}", e.getMessage(), e.getCause());
       }
    }

    private boolean isScenarioMatch(Scenario scenario, SensorsSnapshotAvro snapshot) {
        log.info("Проверка сценария: {}", scenario.getName());
        return scenario.getScenarioConditions().stream()
                .peek(cond -> log.info("Проверка условия для датчика: {}", cond.getSensor().getId()))
                .anyMatch(scenarioCondition ->
                        checkCondition(
                                scenarioCondition.getCondition(),
                                scenarioCondition.getSensor(),
                                snapshot
                        ));
    }

    private boolean checkCondition(Condition condition, Sensor sensor, SensorsSnapshotAvro snapshot) {
        SensorStateAvro record = snapshot.getSensorsState().get(sensor.getId());
        log.info("Проверка полученной записи {} ", record);
        if (record == null) {
            log.warn("Данные для сенсора с id  {} отсутствуют", sensor.getId());
            return false;
        }
        log.info("Обработка показаний датчика {}", record);
        try {
            Object sensorData = record.getData();
            return switch (condition.getType()) {
                case TEMPERATURE -> {
                    int temperature;
                    if (sensorData instanceof TemperatureSensorEventAvro tempData) {
                        temperature = tempData.getTemperatureC();
                    } else if (sensorData instanceof ClimateSensorEventAvro climateData) {
                        temperature = climateData.getTemperatureC();
                    } else {
                        yield false;
                    }
                    yield evaluateCondition(temperature, condition.getOperationType(), condition.getValue());
                }
                case HUMIDITY -> sensorData instanceof ClimateSensorEventAvro tempData &&
                        evaluateCondition(
                                tempData.getHumidity(),
                                condition.getOperationType(),
                                condition.getValue()
                        );
                case CO2LEVEL -> sensorData instanceof ClimateSensorEventAvro tempData &&
                        evaluateCondition(
                                tempData.getCo2Level(),
                                condition.getOperationType(),
                                condition.getValue()
                        );
                case LUMINOSITY -> sensorData instanceof LightSensorEventAvro tempData &&
                        evaluateCondition(
                                tempData.getLuminosity(),
                                condition.getOperationType(),
                                condition.getValue()
                        );
                case MOTION -> sensorData instanceof MotionSensorEventAvro tempData &&
                        evaluateCondition(
                                tempData.getMotion() ? 1 : 0,
                                condition.getOperationType(),
                                condition.getValue()
                        );
                case SWITCH -> sensorData instanceof SwitchSensorEventAvro tempData &&
                        evaluateCondition(
                                tempData.getState() ? 1 : 0,
                                condition.getOperationType(),
                                condition.getValue()
                        );
            };
        } catch (ClassCastException e) {
            log.error("Type mismatch for sensor {}: {}", sensor.getId(), e.getMessage());
            return false;
        }
    }

    private boolean evaluateCondition(int sensorValue, ConditionOperationType operation, int targetValue) {
        return switch (operation) {
            case EQUALS -> sensorValue == targetValue;
            case GREATER_THAN -> sensorValue > targetValue;
            case LOWER_THAN -> sensorValue < targetValue;
        };
    }

    private void sendAction(Scenario scenario, String hubId) {
        scenario.getScenarioActions().forEach(scenarioAction -> {
            Action action = scenarioAction.getAction();
            hubActionSender.send(action, hubId);
            log.info("Отправлено действие {} на хаб {}", action.getId(), hubId);
        });
    }
}
