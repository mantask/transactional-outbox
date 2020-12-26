package lt.kanaporis.documents;

import lombok.Data;
import lombok.experimental.Accessors;
import lt.kanaporis.outbox.DomainEvent;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class DocumentCreatedEvent implements DomainEvent {
    private String name;
    private OffsetDateTime createdOn;
}
