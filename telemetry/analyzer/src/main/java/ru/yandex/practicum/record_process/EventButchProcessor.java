package ru.yandex.practicum.record_process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import ru.yandex.practicum.receiver.OffsetCommitManager;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
public class EventButchProcessor<K, V> implements Consumer<ConsumerRecords<K, V>> {
    private final OffsetCommitManager<K, V> offsetCommitManager;
    private final AtomicBoolean processing;
    private final RecordProcessor<V> recordProcessor;

    @Override
    public void accept(ConsumerRecords<K, V> records) {
        processing.set(true);
        try {
            for (ConsumerRecord<K, V> record : records) {
                try {
                    offsetCommitManager.recordProcessed(record);
                    log.info("Получена запись для обработки {}", record.value());
                    var event = record.value();
                    recordProcessor.process(event);
                } catch (Exception e) {
                    log.error("Ошибка обработки записи {}",  e.getMessage(), e);
                }
            }
        } finally {
            processing.set(false);
        }
    }
}
