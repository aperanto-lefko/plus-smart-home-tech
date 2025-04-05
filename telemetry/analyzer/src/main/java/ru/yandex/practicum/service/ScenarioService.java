package ru.yandex.practicum.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;
import ru.yandex.practicum.model.ScenarioActionId;
import ru.yandex.practicum.model.ScenarioCondition;
import ru.yandex.practicum.model.ScenarioConditionId;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.ScenarioRepository;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScenarioService {
    ScenarioRepository scenarioRepository;
    SensorService sensorService;
    ConditionService conditionService;
    ActionService actionService;


    @Transactional
    public void addScenario(ScenarioAddedEventAvro event, String hubId) {

        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, event.getName())
                .orElseGet(() -> Scenario.builder()
                        .hubId(hubId)
                        .name(event.getName())
                        .build());

        Scenario savedScenario = scenarioRepository.save(scenario);

        // Обрабатываем условия
        processConditions(event.getConditions(), savedScenario);
        // Обрабатываем действия
        processActions(event.getActions(), savedScenario);
        // Второе сохранение - каскадное для связей
        // Можно не вызывать явно, т.к. транзакция закоммитится автоматически

    }

    private void processConditions(List<ScenarioConditionAvro> conditions, Scenario scenario) {
        // 1. Пакетно загружаем сенсоры
        Set<String> sensorIds = conditions.stream()
                .map(ScenarioConditionAvro::getSensorId)
                .collect(Collectors.toSet());
        Map<String, Sensor> sensors = sensorService.findAllByIds(sensorIds);

        // 2. Пакетно сохраняем условия
        List<Condition> savedConditions = conditionService.saveAll(conditions);

        // 3. Создаём связи
        List<ScenarioCondition> scenarioConditions = IntStream.range(0, conditions.size())
                .mapToObj(i -> {
                    ScenarioConditionAvro avro = conditions.get(i);
                    return ScenarioCondition.builder()
                            .id(ScenarioConditionId.builder()
                                    .scenarioId(scenario.getId())
                                    .conditionId(savedConditions.get(i).getId())
                                    .sensorId(avro.getSensorId())
                                    .build())
                            .scenario(scenario)
                            .sensor(sensors.get(avro.getSensorId()))
                            .condition(savedConditions.get(i))
                            .build();
                })
                .toList();
        scenario.getScenarioConditions().addAll(scenarioConditions);
    }

    private void processActions(List<DeviceActionAvro> actions, Scenario scenario) {

        // 1. Пакетно загружаем сенсоры
        Set<String> sensorIds = actions.stream()
                .map(DeviceActionAvro::getSensorId)
                .collect(Collectors.toSet());
        Map<String, Sensor> sensors = sensorService.findAllByIds(sensorIds);
        // 2. Пакетно создаем Action сущности
        // 3. Пакетно сохраняем Action
        List<Action> savedActions = actionService.saveAll(actions);

        // 4. Создаем связи ScenarioAction
        List<ScenarioAction> scenarioActions = IntStream.range(0, actions.size())
                .mapToObj(i -> {
                    DeviceActionAvro avro = actions.get(i);
                    Action action = savedActions.get(i);
                    Sensor sensor = sensors.get(avro.getSensorId());

                    if (sensor == null) {
                        throw new IllegalArgumentException("Sensor not found: " + avro.getSensorId());
                    }

                    return ScenarioAction.builder()
                            .id(ScenarioActionId.builder()
                                    .scenarioId(scenario.getId())
                                    .sensorId(sensor.getId())
                                    .actionId(action.getId())
                                    .build())
                            .scenario(scenario)
                            .sensor(sensor)
                            .action(action)
                            .build();
                })
                .toList();

        // 5. Добавляем все связи сразу
        scenario.getScenarioActions().addAll(scenarioActions);
    }


    @Transactional
    public void removeScenario(String name, String hubId){
        //чтобы каскадно удалить связи - надо их загрузить
        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, name)
                .orElseThrow(() -> new EntityNotFoundException("Сценарий не найден"));
        scenarioRepository.delete(scenario);
    }

}
    /*создание через цикл
    private void processConditions(List<ScenarioConditionAvro> conditions, Scenario scenario) {
        conditions.forEach(conditionAvro -> {
            Sensor sensor = sensorService.findById(conditionAvro.getSensorId());
            Condition condition = conditionService.save(conditionAvro);

            // Создаем ID вручную
            ScenarioConditionId id = new ScenarioConditionId();
            id.setScenarioId(scenario.getId());  // ID сценария уже есть!
            id.setSensorId(sensor.getId());
            id.setConditionId(condition.getId());

            ScenarioCondition scenarioCondition = new ScenarioCondition();
            scenarioCondition.setId(id);
            scenarioCondition.setScenario(scenario);
            scenarioCondition.setSensor(sensor);
            scenarioCondition.setCondition(condition);

            // ID установится автоматически при сохранении благодаря каскаду
            scenario.getScenarioConditions().add(scenarioCondition);
        });
    }*/


    /*создание через цикл
    private void processActions(List<DeviceActionAvro> actions, Scenario scenario) {
        for (DeviceActionAvro actionAvro : actions) {
            // Проверяем существование сенсора
            Sensor sensor = sensorService.findById(actionAvro.getSensorId());
            // Создаем действие
            Action action = actionService.save(actionAvro);
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
    }*/


