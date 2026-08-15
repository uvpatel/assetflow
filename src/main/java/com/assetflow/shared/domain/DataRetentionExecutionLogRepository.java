package com.assetflow.shared.domain;

import java.util.List;
import java.util.UUID;

public interface DataRetentionExecutionLogRepository {
    DataRetentionExecutionLog save(DataRetentionExecutionLog log);
    List<DataRetentionExecutionLog> findByPolicyId(UUID policyId);
}
