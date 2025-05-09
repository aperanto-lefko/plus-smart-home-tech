package ru.yandex.practicum.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.exception.NotMutchException;

@Entity
@Table(name = "scenario_conditions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioCondition {
    @EmbeddedId
    @EqualsAndHashCode.Include
    ScenarioConditionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id", nullable = false)
    @ToString.Exclude
    Scenario scenario;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id", nullable = false)
    @ToString.Exclude
    Sensor sensor;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("conditionId")
    @JoinColumn(name = "condition_id", nullable = false)
    @ToString.Exclude
    Condition condition;

    @PrePersist
    @PreUpdate
    private void validateHubId() {
        if (!scenario.getHubId().equals(sensor.getHubId())) {
            throw new NotMutchException(String.format("id хабов не совпадают для сценария {} и для сенсора {}",
                    scenario.getId(), sensor.getId()));
        }
    }
}
