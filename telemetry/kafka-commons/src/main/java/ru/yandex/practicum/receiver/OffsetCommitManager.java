package ru.yandex.practicum.receiver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
public class OffsetCommitManager <K,V>{
    @Value("${kafka.commit_manager.batch-size}")
    private int batchSize;

    private final KafkaConsumer<K, V> consumer;
    private final ConcurrentHashMap<TopicPartition, OffsetAndMetadata> offsetsToCommit = new ConcurrentHashMap<>();
    private final AtomicInteger processedInBatch = new AtomicInteger(0);

    public void recordProcessed(ConsumerRecord<K, V> record) {
        offsetsToCommit.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1));

        if (processedInBatch.incrementAndGet() % batchSize == 0) {
            commitOffsets();
            processedInBatch.set(0);
        }
    }

    public void commitOffsets() {
        try {
            if (!offsetsToCommit.isEmpty()) {
                consumer.commitSync(offsetsToCommit);
                log.info("Фиксация партии {} офсет", offsetsToCommit.values());
                offsetsToCommit.clear();
            }
        } catch (CommitFailedException e) {
            log.error("Offset commit failed", e);
        } catch (WakeupException e) {
            log.warn("Commit interrupted by WakeupException");
            throw e;
        }
    }
}
