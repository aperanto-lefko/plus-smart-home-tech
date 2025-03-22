package ru.yandex.practicum.service;

import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;

public interface EventService {
    void sendSensorEvent (SensorEvent ev);
    void sendHubEvent (HubEvent ev);
}
