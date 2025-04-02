package ru.yandex.practicum.aggregator;

import java.util.Optional;

public interface RecordProcessor <V,R>{
    Optional<R> process(V record);
}
