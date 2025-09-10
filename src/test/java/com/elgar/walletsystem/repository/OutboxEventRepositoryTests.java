package com.elgar.walletsystem.repository;

import com.elgar.walletsystem.enums.AggregateType;
import com.elgar.walletsystem.enums.TransactionType;
import com.elgar.walletsystem.model.OutboxEvent;
import com.elgar.walletsystem.repository.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
//@Rollback(true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class OutboxEventRepositoryTests {
    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Test
    void findByPublishedAtIsNull_shouldReturnOnlyUnpublishedEvents() {
        // given
        OutboxEvent unpublishedEvent = OutboxEvent.builder()
                .aggregateType(AggregateType.WALLET)
                .aggregateId(UUID.randomUUID())
                .eventType(TransactionType.TOPUP)
                .payload("{\"amount\":100}")
                .publishedAt(null)
                .build();

        OutboxEvent publishedEvent = OutboxEvent.builder()
                .aggregateType(AggregateType.WALLET_TRANSACTION)
                .aggregateId(UUID.randomUUID())
                .eventType(TransactionType.CONSUME)
                .payload("{\"amount\":50}")
                .publishedAt(Instant.now())
                .build();

        outboxEventRepository.save(unpublishedEvent);
        outboxEventRepository.save(publishedEvent);

        // when
        List<OutboxEvent> result = outboxEventRepository.findByPublishedAtIsNull();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEventType()).isEqualTo(TransactionType.TOPUP);
        assertThat(result.get(0).getPublishedAt()).isNull();
    }

    @Test
    void findByPublishedAtIsNull_shouldReturnEmptyListWhenAllPublished() {
        // given
        OutboxEvent publishedEvent = OutboxEvent.builder()
                .aggregateType(AggregateType.WALLET_TRANSACTION)
                .aggregateId(UUID.randomUUID())
                .eventType(TransactionType.CONSUME)
                .payload("{\"amount\":200}")
                .publishedAt(Instant.now())
                .build();

        outboxEventRepository.save(publishedEvent);

        // when
        List<OutboxEvent> result = outboxEventRepository.findByPublishedAtIsNull();

        // then
        assertThat(result).isEmpty();
    }
}