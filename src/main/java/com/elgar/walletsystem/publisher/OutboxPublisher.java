package com.elgar.walletsystem.publisher;

import com.elgar.walletsystem.model.OutboxEvent;
import com.elgar.walletsystem.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxPublisher {
    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelay = 5000) // run every 5 seconds
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByPublishedAtIsNull();

        for (OutboxEvent event : events) {
            try {
                rabbitTemplate.convertAndSend(
                        "wallet.txn.exchange",   // exchange
                        "wallet.txn",            // routing key
                        event.getPayload()       // payload JSON
                );

                event.setPublishedAt(Instant.now());
                outboxEventRepository.save(event);

            } catch (Exception e) {
                // leave unpublished so it retries next run
                System.err.println("❌ Failed to publish event " + event.getId() + ": " + e.getMessage());
            }
        }
    }
}
