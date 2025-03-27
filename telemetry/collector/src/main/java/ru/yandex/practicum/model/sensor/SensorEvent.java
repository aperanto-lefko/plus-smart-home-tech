package ru.yandex.practicum.model.sensor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.enums.SensorEventType;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, //Указывает, что тип объекта будет определяться по его имени (значению поля type в JSON).
        include = JsonTypeInfo.As.EXISTING_PROPERTY, //Указывает, что информация о типе уже существует в JSON как свойство (поле). В данном случае это поле type.
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClimateSensorEvent.class, name = "CLIMATE_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = LightSensorEvent.class, name = "LIGHT_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = MotionSensorEvent.class, name = "MOTION_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = SwitchSensorEvent.class, name = "SWITCH_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = TemperatureSensorEvent.class, name = "TEMPERATURE_SENSOR_EVENT"),
})
@ToString
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class SensorEvent {
    @NotBlank
    String id;
    @NotBlank
    String hubId;
    Instant timestamp = Instant.now();

    @NotNull
    public abstract SensorEventType getType();
}
