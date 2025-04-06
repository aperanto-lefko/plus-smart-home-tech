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

//    @Query("SELECT DISTINCT s FROM Scenario s " +
//            "LEFT JOIN FETCH s.scenarioConditions " +
//            "LEFT JOIN FETCH s.scenarioActions " +
//            "WHERE s.hubId = :hubId AND s.name = :name")
//    Optional<Scenario> findByHubIdAndNameWithRelations(
//            @Param("hubId") String hubId,
//            @Param("name") String name);

    //    @Query("SELECT DISTINCT s FROM Scenario s " +
//            "LEFT JOIN FETCH s.scenarioConditions " +
//            "LEFT JOIN FETCH s.scenarioActions " +
//            "WHERE s.hubId = :hubId")
//    List<Scenario> findByHubIdWithRelations(@Param("hubId") String hubId);
    @EntityGraph(attributePaths = {
            "scenarioConditions",
            "scenarioConditions.condition",  // Загружаем вложенную сущность
            "scenarioActions",
            "scenarioActions.action"
    })
    List<Scenario> findByHubId(String hubId);

    @EntityGraph(attributePaths = {
            "scenarioConditions",
            "scenarioConditions.condition",  // Загружаем вложенную сущность Condition
            "scenarioActions",
            "scenarioActions.action"        // Загружаем вложенную сущность Action
    })
    Optional<Scenario> findByHubIdAndName(
            @Param("hubId") String hubId,
            @Param("name") String name);
}
