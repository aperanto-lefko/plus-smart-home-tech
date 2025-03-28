package ru.yandex.practicum.deserializer;

import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;
import ru.yandex.practicum.exception.DeSerealizationException;

import java.io.IOException;

public class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {
    private final Class<T> targetType;
    private final DatumReader<T> reader;

    public BaseAvroDeserializer(Class<T> targetType) {
        this.targetType = targetType;
        this.reader = new SpecificDatumReader<>(targetType);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            return reader.read(null, decoder);
        } catch (IOException e) {
            throw new DeSerealizationException("Ошибка десереализации", e);
        }
    }
}
