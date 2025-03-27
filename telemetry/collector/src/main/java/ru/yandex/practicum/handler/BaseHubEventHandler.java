package ru.yandex.practicum.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.mapper.HubEventMapper;

@Slf4j
public abstract class BaseHubEventHandler extends BaseEventHandler<HubEventProto>
        implements HubEventHandler {
    @Value("${kafka.topics.hub_events_topic}")
    protected String hubEventsTopic;
    protected final HubEventMapper hubEventMapper;

    protected BaseHubEventHandler(KafkaProducer<String, SpecificRecordBase> kafkaProducer,
                                  HubEventMapper hubEventMapper) {
        super(kafkaProducer);
        this.hubEventMapper = hubEventMapper;
    }

    @Override
    public void handle(HubEventProto event) {
        sendToKafka(event, hubEventMapper::mapToAvro, hubEventsTopic);
    }

    @Override
    public abstract HubEventProto.PayloadCase getMessageType();
}
