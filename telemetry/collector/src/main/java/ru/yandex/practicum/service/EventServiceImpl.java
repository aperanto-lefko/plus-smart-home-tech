package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.SendMessageException;
import ru.yandex.practicum.mapper.HubEventMapper;
import ru.yandex.practicum.mapper.SensorEventMapper;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "kafka.topics")
@Getter
@Setter
@Slf4j
public class EventServiceImpl implements EventService {
    String sensorEventsTopic;
    String hubEventsTopic;
    final KafkaProducer<String, SpecificRecordBase> kPr;
    final HubEventMapper hMp;
    final SensorEventMapper sMp;

    @Override
    public void sendSensorEvent(SensorEvent ev) {
        sendEvent(ev, sMp::mapToAvro, sensorEventsTopic);
    }

    @Override
    public void sendHubEvent(HubEvent ev) {
        sendEvent(ev, hMp::mapToAvro, hubEventsTopic);
    }

    private <T> void sendEvent(T event, Function<T, SpecificRecordBase> mapper, String topic) {
        try {
            SpecificRecordBase avroEvent = mapper.apply(event);
            kPr.send(new ProducerRecord<>(topic, avroEvent));
            log.info("Отправлено сообщение {} в топик  {}", avroEvent, topic);
        } catch (Exception ex) {
            log.error("Ошибка при отправлении сообщения:", ex);
            throw new SendMessageException("Ошибка при отправлении сообщения", ex);
        }
    }
}
