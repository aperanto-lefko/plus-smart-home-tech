package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ActionServiceImpl implements ActionService {
    ActionRepository actionRepository;
    ActionMapper actionMapper;

    @Override
    @Transactional
    public List<Action> saveAll(List<DeviceActionAvro> actions) {
        log.info("Добавление списка действий {}", actions);
        List<Action> savedActions = actionRepository.saveAll(
                actions.stream()
                        .map(actionMapper::toAction)
                        .toList());
        log.info("Действия успешно сохранены {}", savedActions);
        return savedActions;
    }
}
