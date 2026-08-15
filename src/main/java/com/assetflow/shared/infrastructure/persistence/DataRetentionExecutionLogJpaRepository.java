package com.assetflow.shared.infrastructure.persistence;

import com.assetflow.shared.domain.DataRetentionExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DataRetentionExecutionLogJpaRepository extends JpaRepository<DataRetentionExecutionLog, UUID> {
    List<DataRetentionExecutionLog> findByPolicyId(UUID policyId);
}
