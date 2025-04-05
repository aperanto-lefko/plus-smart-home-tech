package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.mapper.ActionMapper;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.repository.ActionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActionServiceImpl implements ActionService{
    ActionRepository actionRepository;
    ActionMapper actionMapper;
@Override
@Transactional
    public List<Action> saveAll(List<DeviceActionAvro> actions) {
        return actionRepository.saveAll(
                actions.stream()
                        .map(actionMapper::toAction)
                        .toList()
        );
    }
}
