package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.ConditionOperationType;
import ru.yandex.practicum.model.ConditionType;

@Mapper(componentModel = "spring")
public interface ConditionMapper {
    @Mapping(target = "type", source = "condition.type")
    @Mapping(target = "operation", source = "condition.operation")
    @Mapping(target = "value", source = "condition.value")
    Condition toCondition(ScenarioConditionAvro condition);

    default ConditionType map(ConditionTypeAvro type) {
        return ConditionType.valueOf(type.name());
    }

    default ConditionOperationType map(ConditionOperationTypeAvro operation) {
        return ConditionOperationType.valueOf(operation.name());
    }

    default Integer mapValue(ScenarioConditionAvro condition) {
        if (condition.getValue() instanceof Integer) {
            return (Integer) condition.getValue();
        } else if (condition.getValue() instanceof Boolean) {
            return (Boolean) condition.getValue() ? 1 : 0;
        }
        return null;
    }
}



