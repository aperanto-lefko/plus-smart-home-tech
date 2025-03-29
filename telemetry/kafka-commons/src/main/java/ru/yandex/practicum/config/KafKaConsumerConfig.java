package ru.yandex.practicum.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.deserializer.DeserializeFactory;
import ru.yandex.practicum.deserializer.DeserializerType;

import java.util.Properties;
import java.util.function.Function;

@Configuration
@ConfigurationProperties(prefix = "kafka.consumer")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Slf4j
@RequiredArgsConstructor
public class KafKaConsumerConfig {
    String bootstrapServer;
    String clientId;
    String groupId;
    String autoOffsetResetConfig;
    Boolean enableAutoCommitConfig;
    String keyDeserializeClass;

    String autoOffsetReset;
    Boolean enableAutoCommit;
    String sessionTimeout;
    String heartbeatInterval;
    String maxPollInterval;
    String fetchMaxWait;
    Integer fetchMinSize;
    Integer fetchMaxBytes;
    Integer maxPartitionFetchBytes;
    Integer maxPollRecords;

    final DeserializeFactory deserializeFactory;

    @Bean
    public Function<DeserializerType, KafkaConsumer<String, SpecificRecordBase>> consumerFactory() {
        return deserializeName -> {
            Properties properties = buildProperties();
            Deserializer<SpecificRecordBase> deserializer = deserializeFactory.createDeserializer(deserializeName);
            log.info("Загруженная конфигурация: " +
                            "bootstrap={}, groupId={}, autoOffsetReset = {}, enableAutoCommit = {}," +
                            "keyDeserializeClass=StringDeserializer, valueDeserializeClass={} ",
                    bootstrapServer, groupId, autoOffsetReset, enableAutoCommit,
                    deserializer.getClass().getName());
            KafkaConsumer<String, SpecificRecordBase> consumer = new KafkaConsumer<>(properties,
                    new StringDeserializer(),
                    deserializer);
            log.info("Успешно создан kafka-consumer");
            return consumer;
        };
    }
    /*
    как вызвать
    @RequiredArgsConstructor
    ****
    private final Function<DeserializerType, KafkaConsumer<String, SpecificRecordBase>> consumerFactory;
    метод
    KafkaConsumer<String, SpecificRecordBase> consumer =
            consumerFactory.apply(DeserializerType.SENSOR_EVENT_DESERIALIZER);
     */

    private Properties buildProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset); // Что делать при отсутствии оффсета: "earliest", "latest", "none"
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit); // Автоматически коммитить оффсеты (true/false)
        //properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializeClass); // Класс для десериализации ключа сообщения
        //properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializeClass);// Класс для десериализации значения сообщения
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout); // Таймаут сессии (мс) - если consumer не отправляет heartbeat дольше этого времени, считается мертвым
        properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatInterval);// Как часто consumer отправляет heartbeat (мс)
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollInterval); // Максимальное время между вызовами poll() (мс)
        properties.put(ConsumerConfig.DEFAULT_FETCH_MAX_WAIT_MS, fetchMaxWait); // Максимальное время ожидания данных при fetch (мс)
        properties.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, fetchMinSize); // Минимальное количество байт для возврата из fetch
        properties.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, fetchMaxBytes); // Максимальное количество байт, возвращаемых за один fetch
        properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes); // Максимальное количество байт, получаемых с одной партиции
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);// Максимальное количество записей, возвращаемых за один poll()
        return properties;
    }
}
