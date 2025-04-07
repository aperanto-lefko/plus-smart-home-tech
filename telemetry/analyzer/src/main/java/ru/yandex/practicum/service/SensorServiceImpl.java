package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
@Slf4j
public class SensorServiceImpl implements SensorService {
    SensorRepository sensorRepository;

    @Override
    public Map<String, Sensor> findAllByIds(Set<String> sensorIds) {
        if (sensorIds == null || sensorIds.isEmpty()) {
            return Collections.emptyMap();
        }
        log.info("Поиск списка sensors по ids");
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
        log.info("Добавление сенсора с id {} с hubId {}", sensorId, hubId);
        if (existsBySensorIdsAndHubId(hubId, sensorId)) {
            return;
        }
        Sensor sensor = sensorRepository.save(Sensor.builder()
                .id(sensorId)
                .hubId(hubId)
                .build());
        log.info("Успешно добавлен сенсор {}", sensor);
    }

    @Override
    @Transactional
    public void removeSensor(String sensorId, String hubId) {
        log.info("Удаление sensor по id {} и hubId {}", sensorId, hubId);
        getSensorByIdAndHubIdWithRelations(sensorId, hubId).ifPresent(sensorRepository::delete);
        //чтобы удалить в связанных таблицах надо явно загрузить связи
    }

    private boolean existsBySensorIdsAndHubId(String hubId, String sensorId) {
        log.info("Поиск совпадений sensors по id {} и hubId {}", sensorId, hubId);
        return sensorRepository.existsByIdInAndHubId(List.of(sensorId), hubId);
    }

    private Optional<Sensor> getSensorByIdAndHubIdWithRelations(String sensorId, String hubId) {
        log.info("Поиск sensors по id {} и hubId {} со связями ", sensorId, hubId);
        return sensorRepository.findByIdAndHubIdWithRelations(sensorId, hubId);
    }

}