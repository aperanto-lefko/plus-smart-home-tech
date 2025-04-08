package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Sensor;

import java.util.Map;
import java.util.Set;

public interface SensorService {
    Map<String, Sensor> findAllByIds(Set<String> sensorIds);

    void addSensor(String sensorId, String hubId);

    void removeSensor(String sensorId, String hubId);
}
