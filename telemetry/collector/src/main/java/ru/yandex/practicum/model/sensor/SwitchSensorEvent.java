package ru.yandex.practicum.model.sensor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.enums.SensorEventType;

@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
//умный переключатель
public class SwitchSensorEvent extends SensorEvent { //Событие датчика переключателя, содержащее информацию о текущем состоянии переключателя
    Boolean state; //состояние переключателя. true - включен, false - выключен.

    @Override
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}
