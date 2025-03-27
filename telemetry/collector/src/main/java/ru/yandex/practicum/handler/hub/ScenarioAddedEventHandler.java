package ru.yandex.practicum.handler.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.handler.BaseHubEventHandler;
import ru.yandex.practicum.mapper.HubEventMapper;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler {
    @Autowired
    public ScenarioAddedEventHandler(KafkaProducer<String, SpecificRecordBase> kafkaProducer,
                                     HubEventMapper hubEventMapper) {
        super(kafkaProducer, hubEventMapper);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }
}
