package com.elgar.walletsystem.model;

import com.elgar.walletsystem.enums.AggregateType;
import com.elgar.walletsystem.enums.TransactionType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.UUID;

    @EqualsAndHashCode(callSuper = true)
    @Entity
    @Table(name="outbox_event")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class OutboxEvent extends BaseEntity {
           @Enumerated(EnumType.STRING)
        @Column(name = "aggregate_type", nullable = false, length = 50)
        private AggregateType aggregateType;

        @Column(name = "aggregate_id", nullable = false, columnDefinition = "uuid")
        private UUID aggregateId;

       @Enumerated(EnumType.STRING)
       @Column(name = "event_type", nullable = false, length = 20)
        private TransactionType eventType;

        @Type(JsonBinaryType.class)
        @Column(columnDefinition = "jsonb", nullable = false)
       private Object payload;

        @Column(name = "published_at")
        private Instant publishedAt;

    }
