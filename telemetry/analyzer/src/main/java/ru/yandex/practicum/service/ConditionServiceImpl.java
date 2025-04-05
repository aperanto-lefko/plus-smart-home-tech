package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
public class ConditionServiceImpl implements ConditionService {
    ConditionRepository conditionRepository;
    ConditionMapper conditionMapper;
@Override
@Transactional
    public List<Condition> saveAll(List<ScenarioConditionAvro> conditions) {
        return conditionRepository.saveAll(
                conditions.stream()
                        .map(conditionMapper::toCondition)
                        .toList()
        );
    }
}
