package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.service.EventService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class EventController {
    final EventService service;

    @PostMapping("/hubs")
    public void sendHubEvent(@Valid @RequestBody HubEvent ev) {
        log.info("Получено событие датчика HubEvent {}", ev);
        service.sendHubEvent(ev);
    }

    @PostMapping("/sensors")
    public void sendSensorEvent(@Valid @RequestBody SensorEvent ev) {
        log.info("Получено событие датчика SensorEvent {}", ev);
        service.sendSensorEvent(ev);
    }
}
