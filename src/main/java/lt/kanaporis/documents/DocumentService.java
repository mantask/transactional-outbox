package lt.kanaporis.documents;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lt.kanaporis.outbox.DomainEventEnvelope;
import lt.kanaporis.outbox.DomainEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Transactional
@Service
@AllArgsConstructor
public class DocumentService {
    private final DomainEventPublisher publisher;
    private final DocumentRepository documentRepository;
    private final CountDownLatch documentLatch;

    public void create() {
        var document = new Document("Implementing the Outbox Pattern");
        documentRepository.save(document);
        publisher.publish(document, new DocumentCreatedEvent()
            .setName(document.getName())
            .setCreatedOn(OffsetDateTime.now()));
    }

    @EventListener
    public void on(DomainEventEnvelope<DocumentCreatedEvent> ee) {
        log.info("Outbox event received async: eventType={}, aggregateId={}, aggregateType={}", ee.getType(), ee.getAggregateId(), ee.getAggregateType());
        documentLatch.countDown();
    }
}
