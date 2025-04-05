package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.mapper.ConditionMapper;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.repository.ConditionRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConditionService {
    ConditionRepository conditionRepository;
    ConditionMapper mapper;

    public Condition save(ScenarioConditionAvro conditionAvro) {
        Condition condition = mapper.toCondition(conditionAvro);
        return conditionRepository.save(condition);
    }
}
