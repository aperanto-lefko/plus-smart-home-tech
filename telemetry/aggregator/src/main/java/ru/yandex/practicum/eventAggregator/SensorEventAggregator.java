package ru.yandex.practicum.eventAggregator;

import org.apache.avro.specific.SpecificRecord;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SensorEventAggregator {
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        return Optional.ofNullable(event)
                .map(e -> {
                    SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(
                            event.getHubId(),
                            hubId -> new SensorsSnapshotAvro(
                                    hubId,
                                    event.getTimestamp(),
                                    new HashMap<>()
                            )
                    );
                    return Optional.ofNullable(snapshot.getSensorsState().get(e.getId()))
                            .filter(oldState ->
                                    oldState.getTimestamp().isAfter(e.getTimestamp()) ||
                                            dataEquals(
                                                    (SpecificRecord) oldState.getData(),
                                                    (SpecificRecord) e.getEvent())
                            )
                            .map(__ -> (SensorsSnapshotAvro) null) //если фильтр сработал возвращаем null
                            .orElseGet(() -> { //если не сработал - создаем новое SensorStateAvro
                                        SensorStateAvro newState = new SensorStateAvro(e.getTimestamp(), e.getEvent());
                                        snapshot.getSensorsState().put(e.getId(), newState);
                                        snapshot.setTimestamp(e.getTimestamp());
                                        return snapshot;
                                    }
                            );

                });
    }

    private boolean dataEquals(SpecificRecord oldData, SpecificRecord newData) {
        return switch (oldData) {
            case null -> newData == null;
            case SpecificRecord o when newData == null -> false;
            case SpecificRecord o when o.getClass() != newData.getClass() -> false;
            default -> oldData.equals(newData);
        };
    }
}