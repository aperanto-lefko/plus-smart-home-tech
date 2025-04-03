package ru.yandex.practicum.aggregator;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.SnapshotHandler;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class BaseAggregator <K, V, R> {
    protected final KafkaConsumerManager<K, V> consumerManager;
    protected final RecordProcessor<V, R> recordProcessor;
    protected final OffsetCommitManager<K, V> offsetCommitManager;
    protected final SnapshotHandler<R> snapshotHandler;
    protected final AtomicBoolean processing = new AtomicBoolean(false);

    protected abstract List<String> getInputTopics();
    protected abstract Consumer<ConsumerRecords<K, V>> createBatchProcessor();

    @PostConstruct
    public void start() {
        consumerManager.subscribe(getInputTopics());
        consumerManager.startPolling(createBatchProcessor());
    }

    @PreDestroy
    public void shutdown() {
        consumerManager.shutdown();
        waitForCompletion();
    }
    private void waitForCompletion() {
        int retries = 0;
        while (processing.get() && retries++ < 5) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
