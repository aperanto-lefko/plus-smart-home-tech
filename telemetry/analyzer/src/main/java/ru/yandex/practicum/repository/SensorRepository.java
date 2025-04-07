package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Sensor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, String> {
    boolean existsByIdInAndHubId(Collection<String> ids, String hubId);

    @Query("SELECT DISTINCT s FROM Sensor s " +
            "LEFT JOIN FETCH s.scenarioActions " +
            "LEFT JOIN FETCH s.scenarioConditions " +
            "WHERE s.id = :sensorId AND s.hubId = :hubId")
    Optional<Sensor> findByIdAndHubIdWithRelations(
            @Param("sensorId") String sensorId,
            @Param("hubId") String hubId
    );


    List<Sensor> findAllByIdIn(Collection<String> ids);
}
