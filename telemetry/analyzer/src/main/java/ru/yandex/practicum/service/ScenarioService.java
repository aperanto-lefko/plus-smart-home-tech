package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.mapper.ScenarioMapper;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;
import ru.yandex.practicum.model.ScenarioActionId;
import ru.yandex.practicum.model.ScenarioCondition;
import ru.yandex.practicum.model.ScenarioConditionId;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScenarioService {
    ScenarioMapper scenarioMapper;
    ScenarioRepository scenarioRepository;
    SensorService sensorService;
    ConditionService conditionService;
    //заменить на сервисы
    ConditionRepository conditionRepository; //заменить на сервисы
    ActionRepository actionRepository; //заменить на сервисы

    @Transactional
    public void addScenario(ScenarioAddedEventAvro event, String hubId) {

        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, event.getName())
                .orElseGet(() -> Scenario.builder()
                        .hubId(hubId)
                        .name(event.getName())
                        .build());

        Scenario savedScenario = scenarioRepository.save(scenario);
        // Очищаем существующие условия и действия (если это обновление)
        scenario.getScenarioConditions().clear();
        scenario.getScenarioActions().clear();

        // Обрабатываем условия
        processConditions(event.getConditions(), savedScenario);

        // Обрабатываем действия
        processActions(event.getActions(), savedScenario);


    }

    private void processConditions(List<ScenarioConditionAvro> conditions, Scenario scenario) {
        conditions.forEach(conditionAvro -> {
            Sensor sensor = sensorService.findById(conditionAvro.getSensorId());
            Condition condition = conditionService.save(conditionAvro);

            ScenarioCondition scenarioCondition = new ScenarioCondition();
            scenarioCondition.setScenario(scenario);
            scenarioCondition.setSensor(sensor);
            scenarioCondition.setCondition(condition);

            // ID установится автоматически при сохранении благодаря каскаду
            scenario.getScenarioConditions().add(scenarioCondition);
        });
    }

    private void processActions(HubEventAvro event, Scenario scenario) {
        for (DeviceActionAvro actionAvro : event.getEvent().getActions()) {
            // Проверяем существование сенсора
            Sensor sensor = sensorRepository.findById(actionAvro.getSensorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sensor not found: " + actionAvro.getSensorId()));

            // Создаем действие
            Action action = scenarioMapper.toAction(actionAvro);

            // Создаем связь сценарий-сенсор-действие
            ScenarioAction scenarioAction = ScenarioAction.builder()
                    .id(ScenarioActionId.builder()
                            .scenarioId(scenario.getId())
                            .sensorId(sensor.getId())
                            .actionId(action.getId())
                            .build())
                    .scenario(scenario)
                    .sensor(sensor)
                    .action(action)
                    .build();

            // Добавляем в коллекцию (каскад сохранит action)
            scenario.getScenarioActions().add(scenarioAction);
        }
    }
}
