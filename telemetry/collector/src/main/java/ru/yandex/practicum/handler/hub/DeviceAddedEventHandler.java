package ru.yandex.practicum.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.handler.HubEventHandler;
@Component
public class DeviceAddedEventHandler implements HubEventHandler {
   @Override
   public HubEventProto.PayloadCase getMessageType() {
       return HubEventProto.PayloadCase.DEVICE_ADDED;
   }
    @Override
    public void handle(HubEventProto event) {

    }
}
