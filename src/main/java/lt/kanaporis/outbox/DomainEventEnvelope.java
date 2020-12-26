package lt.kanaporis.outbox;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class DomainEventEnvelope<T extends DomainEvent> implements ResolvableTypeProvider {
    private UUID id;
    private UUID aggregateId;
    private String aggregateType;
    private String type;
    private T event;
    private OffsetDateTime createdOn;

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(event));
    }
}
