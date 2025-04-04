package ru.yandex.practicum.record_process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import ru.yandex.practicum.receiver.OffsetCommitManager;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
public class SnapshotButchProcessor<K,V> implements Consumer<ConsumerRecords<K, V>> {
    private final OffsetCommitManager<K, V> offsetCommitManager;
    private final AtomicBoolean processing;
    @Override
    public void accept(ConsumerRecords<K, V> records) {
        processing.set(true);
        try {
            for (ConsumerRecord<K, V> record : records) {
                try {
                    offsetCommitManager.recordProcessed(record);
                    log.info("Получена снапшот для обработки {}", record.value());
                    //обработка одного снапшота - результат в boolean
                    //если boolean - отправка
//                    Optional<R> result = recordProcessor.process(record.value());
//                    result.ifPresent(snapshotHandler::handle);
                } catch (Exception e) {
                    log.error("Ошибка обработки снапшота", e);
                }
            }
        } finally {
            processing.set(false);
        }
    }
}
