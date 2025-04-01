package ru.yandex.practicum.eventAggregator;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@Component
public class SensorSnapshotUpdater {
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

       public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        if (event == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(event)
                .map(e -> {
                    // Получаем или создаем новый снимок для хаба
                    SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(
                            e.getHubId(),
                            hubId -> new SensorsSnapshotAvro(
                                    hubId,
                                    e.getTimestamp(),
                                    new HashMap<>()
                            )
                    );

                    // Проверяем, нужно ли обновлять состояние
                    SensorStateAvro oldState = snapshot.getSensorsState().get(e.getId());

                    // Если состояние уже существует и либо его timestamp новее,
                    // либо данные совпадают - не обновляем
                    if (oldState != null &&
                            (oldState.getTimestamp().isAfter(e.getTimestamp()) ||
                                    dataEquals((SpecificRecord)oldState.getData(), (SpecificRecord)e.getEvent()))) {
                        return null;
                    }

                    // Обновляем состояние
                    SensorStateAvro newState = new SensorStateAvro(e.getTimestamp(), e.getEvent());
                    snapshot.getSensorsState().put(e.getId(), newState);
                    snapshot.setTimestamp(e.getTimestamp());
                    return snapshot;
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