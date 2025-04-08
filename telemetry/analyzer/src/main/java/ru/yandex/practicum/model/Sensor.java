package ru.yandex.practicum.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "sensors")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"scenarioConditions", "scenarioActions"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Sensor {
    @Id
    @EqualsAndHashCode.Include
    String id;
    @Column(name = "hub_id", nullable = false)
    String hubId;
    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<ScenarioCondition> scenarioConditions = new HashSet<>();
    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<ScenarioAction> scenarioActions = new HashSet<>();
}
