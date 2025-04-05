package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.ConditionOperationType;
import ru.yandex.practicum.model.ConditionType;

@Mapper(componentModel = "spring")
public interface ConditionMapper {
    @Mapping(target = "type", source = "condition.type")
    @Mapping(target = "operationType", source = "condition.operation")
    @Mapping(target = "value", source = "condition.value", qualifiedByName = "mapValue")
    Condition toCondition(ScenarioConditionAvro condition);

    default ConditionType map(ConditionTypeAvro type) {
        return ConditionType.valueOf(type.name());
    }

    default ConditionOperationType map(ConditionOperationTypeAvro operation) {
        return ConditionOperationType.valueOf(operation.name());
    }
    @Named("mapValue")
    default Integer mapValue(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        return null;
    }
}



