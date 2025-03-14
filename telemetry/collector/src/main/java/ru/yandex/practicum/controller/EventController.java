package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.service.EventService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventController {
    final EventService service;

    @PostMapping
    public void sendHubEvent(@Valid @RequestBody HubEvent ev) {
        service.sendHubEvent(ev);
    }

    @PostMapping
    public void sendSensorEvent(@Valid @RequestBody SensorEvent ev) {
        service.sendSensorEvent(ev);
    }
}
