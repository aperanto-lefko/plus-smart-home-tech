package ru.yandex.practicum.model.hub.scenario;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.enums.ConditionOperationType;
import ru.yandex.practicum.enums.ConditionType;

@ToString
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioCondition {//Условие сценария, которое содержит информацию о датчике, типе условия, операции и значении.
    String sensorId;
    ConditionType type;
    ConditionOperationType operation;
    Integer value;
}
