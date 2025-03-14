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
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.SendMessageException;
import ru.yandex.practicum.mapper.HubEventMapper;
import ru.yandex.practicum.mapper.SensorEventMapper;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "kafka.topics")
@Getter
@Setter
@Slf4j
public class EventServiceImpl {
    final String sensorEventsTopic;
    final String hubEventsTopic;
    final KafkaProducer<String, SpecificRecordBase> kPr;
    final HubEventMapper hMp;
    final SensorEventMapper sMp;

    public void sendSensorEvent (SensorEvent ev) {
        SpecificRecordBase avroEvent = sMp.mapToAvro(ev);
        sendEvent(avroEvent, sensorEventsTopic);
    }
    public void sendHubEvent (HubEvent ev) {
        SpecificRecordBase avroEvent = hMp.mapToAvro(ev);
        sendEvent(avroEvent, hubEventsTopic);
    }

    public void sendEvent(SpecificRecordBase avroEvent, String topic) {
        try {
            kPr.send(new ProducerRecord<>(topic, avroEvent));
            log.info("Отправлено сообщение {} в топик  {}", avroEvent, topic);
        } catch (Exception ex) {
            log.error("Ошибка при отправлении сообщения:", ex );
            throw new SendMessageException("Ошибка при отправлении сообщения", ex );
        }
    }

}
