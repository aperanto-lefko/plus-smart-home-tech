package ru.yandex.practicum.model.hub.device;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.enums.HubEventType;


import java.time.Instant;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceAddedEvent.class, name = "DEVICE_ADDED"),
        @JsonSubTypes.Type(value = DeviceRemovedEvent.class, name = "DEVICE_REMOVED"),
})
@ToString
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class HubDeviceEvent {
    @NotBlank
    String id;
    @NotBlank
    String hubId;
    Instant timestamp = Instant.now();
    @NotNull
    public abstract HubEventType getType();
}
