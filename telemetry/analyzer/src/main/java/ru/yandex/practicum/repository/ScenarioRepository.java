package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Scenario;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

    /*
        @EntityGraph(attributePaths = {
                "scenarioConditions",
                "scenarioConditions.condition",
                "scenarioConditions.sensor",
                "scenarioActions",
                "scenarioActions.action",
                "scenarioActions.sensor",
                "scenarioActions.action.scenarioActions"
        })
        List<Scenario> findByHubId(String hubId);
    */
    @Query("""
                SELECT DISTINCT s FROM Scenario s
                LEFT JOIN FETCH s.scenarioConditions sc
                LEFT JOIN FETCH sc.condition
                LEFT JOIN FETCH sc.sensor
                LEFT JOIN FETCH s.scenarioActions sa
                LEFT JOIN FETCH sa.action
                LEFT JOIN FETCH sa.sensor
                LEFT JOIN FETCH sa.action.scenarioActions
                WHERE s.hubId = :hubId
            """)
    List<Scenario> findByHubIdWithFullGraph(@Param("hubId") String hubId);

    /*
        @EntityGraph(attributePaths = {
                "scenarioConditions",
                "scenarioConditions.condition",
                "scenarioConditions.sensor",
                "scenarioActions",
                "scenarioActions.action",
                "scenarioActions.sensor",
                "scenarioActions.action.scenarioActions"
        })
        Optional<Scenario> findByHubIdAndName(
                @Param("hubId") String hubId,
                @Param("name") String name);
    }
    */
    @Query("""
                SELECT DISTINCT s FROM Scenario s
                LEFT JOIN FETCH s.scenarioConditions sc
                LEFT JOIN FETCH sc.condition
                LEFT JOIN FETCH sc.sensor
                LEFT JOIN FETCH s.scenarioActions sa
                LEFT JOIN FETCH sa.action
                LEFT JOIN FETCH sa.sensor
                LEFT JOIN FETCH sa.action.scenarioActions
                WHERE s.hubId = :hubId AND s.name = :name
            """)
    Optional<Scenario> findByHubIdAndName(
            @Param("hubId") String hubId,
            @Param("name") String name);
}