package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mapper.HubEventMapper;
import ru.yandex.practicum.mapper.SensorEventMapper;

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

    public void

}
