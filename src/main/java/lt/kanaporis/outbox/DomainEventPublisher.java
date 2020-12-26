package lt.kanaporis.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class DomainEventPublisher {
    private final DomainEventDao domainEventDao;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void publish(@NonNull AbstractPersistable<UUID> aggregate,
                        @NonNull DomainEvent event) {
        domainEventDao.pushToOutbox(
            aggregate.getId(),
            aggregate.getClass().getName(),
            event.getClass().getName(),
            objectMapper.writeValueAsString(event));
        log.info("Domain event saved to outbox: eventType={}, aggregateType={}, aggregateId={}", event.getClass().getName(), aggregate.getClass().getName(), aggregate.getId());
    }
}
