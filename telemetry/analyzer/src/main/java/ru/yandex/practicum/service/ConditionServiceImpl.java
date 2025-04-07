package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.mapper.ConditionMapper;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.repository.ConditionRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ConditionServiceImpl implements ConditionService {
    ConditionRepository conditionRepository;
    ConditionMapper conditionMapper;

    @Override
    @Transactional
    public List<Condition> saveAll(List<ScenarioConditionAvro> conditions) {
        log.info("Добавление списка условий {}", conditions);
        List<Condition> savedConditions = conditionRepository.saveAll(
                conditions.stream()
                        .map(conditionMapper::toCondition)
                        .toList());
        log.info("Условия успешно сохранены {}", savedConditions);
        return savedConditions;
    }
}
