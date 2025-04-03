package ru.yandex.practicum.record_process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j

public class SensorEventProcessor implements RecordProcessor<SensorEventAvro, SensorsSnapshotAvro> {
    private final SensorSnapshotUpdater updater;

    @Override
    public Optional<SensorsSnapshotAvro> process(SensorEventAvro record) {
        log.info("Верификация сообщения {}", record);
        return updater.updateState(record);
    }
}

