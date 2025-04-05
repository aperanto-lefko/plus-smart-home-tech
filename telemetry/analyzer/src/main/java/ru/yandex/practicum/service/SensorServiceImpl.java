package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class SensorServiceImpl implements SensorService {
    SensorRepository sensorRepository;
    @Override
    public Map<String, Sensor> findAllByIds(Set<String> sensorIds) {
        if (sensorIds == null || sensorIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Sensor> sensors = sensorRepository.findAllByIdIn(sensorIds);
        return sensors.stream()
                .collect(Collectors.toMap(
                        Sensor::getId,
                        Function.identity()
                ));
    }
    @Override
    @Transactional
    public void addSensor(String sensorId, String hubId) {
        if (existsBySensorIdsAndHubId(hubId, sensorId)) {
            return;
        }
        sensorRepository.save(Sensor.builder()
                .id(sensorId)
                .hubId(hubId)
                .build());
    }
    @Override
    @Transactional
    public void removeSensor(String sensorId, String hubId) {
        getSensorByIdAndHubId(sensorId, hubId).ifPresent(sensorRepository::delete);
    }

    private boolean existsBySensorIdsAndHubId(String hubId, String sensorId) {
        return sensorRepository.existsByIdInAndHubId(List.of(sensorId), hubId);
    }

    private Optional<Sensor> getSensorByIdAndHubId(String sensorId, String hubId) {
        return sensorRepository.findByIdAndHubId(sensorId, hubId);
    }

}