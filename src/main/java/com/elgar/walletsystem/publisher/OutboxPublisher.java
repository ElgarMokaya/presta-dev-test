package com.elgar.walletsystem.publisher;

import com.elgar.walletsystem.config.properties.QueueConfiguration;
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
    private final QueueConfiguration queueConfiguration;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByPublishedAtIsNull();

        for (OutboxEvent event : events) {
            try {
                rabbitTemplate.convertAndSend(
                       queueConfiguration.getWallet().getExchange(),
                        queueConfiguration.getWallet().getRoutingKey(),
                        event.getPayload()
                );

                event.setPublishedAt(Instant.now());
                outboxEventRepository.save(event);

            } catch (Exception e) {

                System.err.println("Failed to publish event " + event.getId() + ": " + e.getMessage());
            }
        }
    }
}
