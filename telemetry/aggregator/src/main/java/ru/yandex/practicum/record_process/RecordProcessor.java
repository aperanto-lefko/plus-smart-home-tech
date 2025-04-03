package ru.yandex.practicum.record_process;

import java.util.Optional;

public interface RecordProcessor <V,R>{
    Optional<R> process(V record);
}
