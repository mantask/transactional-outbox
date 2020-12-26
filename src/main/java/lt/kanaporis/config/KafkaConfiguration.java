package lt.kanaporis.config;

import lombok.AllArgsConstructor;
import lt.kanaporis.outbox.DomainEventConsumer;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@EnableKafka
@Configuration
@AllArgsConstructor
public class KafkaConfiguration {
    private final DomainEventConsumer domainEventConsumer;

    @KafkaListener(topicPattern = "outbox.event.*")
    public void handleInboxMessage(@Payload String eventPayload,
                                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String aggregateId,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String aggregateType,
                                   @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long createdOn,
                                   @Header(DomainEventConsumer.HEADER_EVENT_ID) String eventId,
                                   @Header(DomainEventConsumer.HEADER_EVENT_TYPE) String eventType,
                                   Acknowledgment ack) {
        domainEventConsumer.consume(
            StringEscapeUtils.unescapeJson(eventPayload.substring(1, eventPayload.length() - 1)),
            UUID.fromString(aggregateId),
            aggregateType,
            OffsetDateTime.ofInstant(Instant.ofEpochSecond(createdOn), ZoneOffset.UTC),
            UUID.fromString(eventId),
            eventType.replace("outbox.event.", ""));
        ack.acknowledge();
    }
}
