package com.assetflow.notification.infrastructure;

import com.assetflow.notification.domain.OutboxEvent;
import com.assetflow.notification.domain.OutboxEventRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

interface SpringDataOutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    @Query("SELECT e FROM OutboxEvent e WHERE e.status = 'PENDING' ORDER BY e.createdAt ASC")
    List<OutboxEvent> findPendingEvents();
}

@Repository
public class JpaOutboxEventRepositoryAdapter implements OutboxEventRepository {

    private final SpringDataOutboxEventRepository repository;

    public JpaOutboxEventRepositoryAdapter(SpringDataOutboxEventRepository repository) {
        this.repository = repository;
    }

    @Override
    public OutboxEvent save(OutboxEvent event) {
        return repository.save(event);
    }

    @Override
    public List<OutboxEvent> findPendingEvents() {
        return repository.findPendingEvents();
    }
}
