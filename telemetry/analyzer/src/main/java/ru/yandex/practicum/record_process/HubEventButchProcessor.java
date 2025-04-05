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
public class HubEventButchProcessor<K,V> implements Consumer<ConsumerRecords<K, V>> {
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
                    log.info("Получена снапшот для обработки {}", record.value());
                    //обработка одного event - сохранение удаление
                    var event = record.value();
                    recordProcessor.process(event);
                    //далее работа с базой данных
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
