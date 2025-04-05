package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SensorService {
    SensorRepository sensorRepository;

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
}
