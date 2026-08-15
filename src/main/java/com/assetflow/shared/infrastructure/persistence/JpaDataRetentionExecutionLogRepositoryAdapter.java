package com.assetflow.shared.infrastructure.persistence;

import com.assetflow.shared.domain.DataRetentionExecutionLog;
import com.assetflow.shared.domain.DataRetentionExecutionLogRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JpaDataRetentionExecutionLogRepositoryAdapter implements DataRetentionExecutionLogRepository {

    private final DataRetentionExecutionLogJpaRepository jpaRepository;

    public JpaDataRetentionExecutionLogRepositoryAdapter(DataRetentionExecutionLogJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public DataRetentionExecutionLog save(DataRetentionExecutionLog log) {
        return jpaRepository.save(log);
    }

    @Override
    public List<DataRetentionExecutionLog> findByPolicyId(UUID policyId) {
        return jpaRepository.findByPolicyId(policyId);
    }
}
