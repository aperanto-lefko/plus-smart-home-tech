package ru.yandex.practicum.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import ru.yandex.practicum.exception.SendMessageException;
import ru.yandex.practicum.exception.SerializationException;

import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public abstract class KafkaEventSender<T> {
    protected final KafkaProducer<String, SpecificRecordBase> kafkaProducer;

    protected void sendToKafka(T event, Function<T, SpecificRecordBase> mapper, String topic) {
        SpecificRecordBase avroEvent = mapper.apply(event);
        send(avroEvent, topic);
    }

    protected void sendToKafka(SpecificRecordBase avroEvent, String topic) {
        send(avroEvent, topic);
    }

    private void send(SpecificRecordBase avroEvent, String topic) {
        try {
            kafkaProducer.send(new ProducerRecord<>(topic, avroEvent),
                    (metadata, e) -> {
                        if (e != null) {
                            log.error("[{}] Ошибка отправки: {}", topic, e.getMessage());
                        } else {
                            log.info("Отправлено в {} - {}", topic, metadata.partition());
                        }
                    });
        } catch (SerializationException | KafkaException ex) {
            log.error("Ошибка при отправлении сообщения:", ex);
            throw new SendMessageException("Ошибка при отправлении сообщения", ex);
        }
    }
}

