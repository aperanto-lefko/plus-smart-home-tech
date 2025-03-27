package ru.yandex.practicum.kafka.deserializer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;

public class BaseAvroDeserializer <T extends SpecificRecordBase> implements Deserializer<T> {

    @Override
    public T deserialize(String topic, byte[] data)
    {
       return null;
    }

}
