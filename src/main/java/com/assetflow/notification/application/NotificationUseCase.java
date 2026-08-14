package com.assetflow.notification.application;

import com.assetflow.notification.domain.OutboxEvent;
import com.assetflow.notification.domain.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationUseCase {

    private final OutboxEventRepository repository;

    public NotificationUseCase(OutboxEventRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void publishEvent(UUID organizationId, String eventType, String payload) {
        OutboxEvent event = new OutboxEvent(organizationId, eventType, payload);
        repository.save(event);
    }

    @Transactional
    public void processPendingEvents() {
        List<OutboxEvent> pending = repository.findPendingEvents();
        for (OutboxEvent event : pending) {
            event.markProcessed();
            repository.save(event);
        }
    }
}
