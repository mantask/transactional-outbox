package lt.kanaporis.outbox;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
@AllArgsConstructor
class DomainEventDao {
    private final JdbcTemplate jdbcTemplate;

    public void pushToOutbox(UUID aggregateId,
                             String aggregateType,
                             String eventType,
                             String eventPayload) {
        var eventId = UUID.randomUUID();
        jdbcTemplate.update("insert into msg_outbox(id, aggregateid, aggregatetype, type, payload) values (?, ?, ?, ?, ?)", eventId, aggregateId, aggregateType, eventType, eventPayload);
        jdbcTemplate.update("delete from msg_outbox where id = ?", eventId);
    }

    public void fallbackToDlq(UUID eventId,
                              UUID aggregateId,
                              String aggregateType,
                              String eventType,
                              String eventPayload,
                              OffsetDateTime createdOn) {
        jdbcTemplate.update("insert into msg_inbox_dlq(id, aggregateid, aggregatetype, type, payload, ts) values (?, ?, ?, ?, ?, ?) on conflict (id) do nothing",
            eventId, aggregateId, aggregateType, eventType, eventPayload, createdOn);
    }

    public boolean isProcessed(UUID msgId) {
        return jdbcTemplate.queryForObject("select exists(select * from msg_inbox where id = ?)", Boolean.class, msgId);
    }

    public void markProcessed(UUID msgId) {
        jdbcTemplate.update("insert into msg_inbox(id, created_on) values (?, now())", msgId);
    }
}
