package lt.kanaporis.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class DomainEventConsumer {
    public static final String HEADER_EVENT_ID = "id";
    public static final String HEADER_EVENT_TYPE = "type";
    private final ApplicationEventPublisher publisher;
    private final ObjectMapper objectMapper;
    private final DomainEventDao domainEventDao;

    @Transactional
    public void consume(String eventPayloadJson,
                        UUID aggregateId,
                        String aggregateType,
                        OffsetDateTime createdOn,
                        UUID eventId,
                        String eventType) {
        try {
            if (domainEventDao.isProcessed(eventId)) return;
            var eventClass = getClass(eventType);
            if (eventClass == null) return;
            var event = objectMapper.readValue(eventPayloadJson, eventClass);
            publisher.publishEvent(new DomainEventEnvelope<>()
                .setId(eventId)
                .setAggregateId(aggregateId)
                .setAggregateType(aggregateType)
                .setType(eventType)
                .setEvent(event)
                .setCreatedOn(createdOn));
            domainEventDao.markProcessed(eventId);
        } catch (IOException e) {
            log.error("Failed to map event from a message -- adding to DLQ: eventType={}, aggregateType={}, aggregateId={}", eventType, aggregateType, aggregateId, e);
            domainEventDao.fallbackToDlq(eventId,
                aggregateId,
                aggregateType,
                eventType,
                eventPayloadJson,
                createdOn);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends DomainEvent> getClass(String name) {
        try {
            return (Class<? extends DomainEvent>) Class.forName(name);
        } catch (ClassNotFoundException | ClassCastException e) {
            return null;
        }
    }
}
