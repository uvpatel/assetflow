package com.assetflow.webhook.infrastructure;

import com.assetflow.webhook.domain.WebhookSubscription;
import com.assetflow.webhook.domain.WebhookSubscriptionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataWebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, UUID> {
    List<WebhookSubscription> findByOrganizationId(UUID organizationId);
}

@Repository
public class JpaWebhookSubscriptionRepositoryAdapter implements WebhookSubscriptionRepository {

    private final SpringDataWebhookSubscriptionRepository repository;

    public JpaWebhookSubscriptionRepositoryAdapter(SpringDataWebhookSubscriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public WebhookSubscription save(WebhookSubscription subscription) {
        return repository.save(subscription);
    }

    @Override
    public Optional<WebhookSubscription> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<WebhookSubscription> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
