package ru.yandex.practicum.record_process;

public interface RecordProcessor <V>{
    void process(V record);
}
