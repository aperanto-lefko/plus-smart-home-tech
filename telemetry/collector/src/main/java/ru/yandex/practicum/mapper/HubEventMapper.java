package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassMapping;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.device.DeviceAddedEvent;
import ru.yandex.practicum.model.hub.device.DeviceRemovedEvent;
import ru.yandex.practicum.model.hub.scenario.ScenarioAddedEvent;
import ru.yandex.practicum.model.hub.scenario.ScenarioRemovedEvent;

@Mapper(componentModel = "spring")
public interface HubEventMapper {

    @Mapping(target = "event", source = ".", qualifiedByName = "mapClass")
    HubEventAvro mapToAvro(HubEvent event);

    @Named("mapClass")
    @SubclassMapping(source = DeviceAddedEvent.class, target = DeviceAddedEventAvro.class)
    @SubclassMapping(source = DeviceRemovedEvent.class, target = DeviceRemovedEventAvro.class)
    @SubclassMapping(source = ScenarioAddedEvent.class, target = ScenarioAddedEventAvro.class)
    @SubclassMapping(source = ScenarioRemovedEvent.class, target = ScenarioRemovedEventAvro.class)
    Object mapClass(HubEvent event);

    DeviceAddedEventAvro mapToAvro(DeviceAddedEvent event);

    DeviceRemovedEventAvro mapToAvro(DeviceRemovedEvent event);

    ScenarioAddedEventAvro mapToAvro(ScenarioAddedEvent event);

    ScenarioRemovedEventAvro mapToAvro(ScenarioRemovedEvent event);
}
