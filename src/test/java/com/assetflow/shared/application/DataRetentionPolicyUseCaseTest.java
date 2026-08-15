package com.assetflow.shared.application;

import com.assetflow.shared.domain.DataRetentionExecutionLog;
import com.assetflow.shared.domain.DataRetentionExecutionLogRepository;
import com.assetflow.shared.domain.DataRetentionPolicy;
import com.assetflow.shared.domain.DataRetentionPolicyRepository;
import com.assetflow.shared.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataRetentionPolicyUseCaseTest {

    @Mock
    private DataRetentionPolicyRepository repository;

    @Mock
    private DataRetentionExecutionLogRepository executionLogRepository;

    private DataRetentionPolicyUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new DataRetentionPolicyUseCase(repository, executionLogRepository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should execute retention policy, record execution log, and update policy stats")
    void shouldExecuteRetentionPolicyAndRecordLog() {
        UUID policyId = UUID.randomUUID();
        DataRetentionPolicy policy = DataRetentionPolicy.create(
                orgId,
                "AUDIT_LOGS",
                365,
                DataRetentionPolicy.RetentionAction.ARCHIVE
        );

        when(repository.findById(policyId)).thenReturn(Optional.of(policy));
        when(repository.save(any(DataRetentionPolicy.class))).thenAnswer(inv -> inv.getArgument(0));
        when(executionLogRepository.save(any(DataRetentionExecutionLog.class))).thenAnswer(inv -> inv.getArgument(0));

        DataRetentionExecutionLog executionLog = useCase.executeRetentionPolicy(orgId, policyId);

        assertThat(executionLog.getOrganizationId()).isEqualTo(orgId);
        assertThat(executionLog.getPolicyId()).isEqualTo(policyId);
        assertThat(executionLog.getTargetModule()).isEqualTo("AUDIT_LOGS");
        assertThat(executionLog.getAction()).isEqualTo("ARCHIVE");
        assertThat(executionLog.getRecordsAffected()).isGreaterThan(0);
        assertThat(policy.getRecordsProcessed()).isEqualTo(executionLog.getRecordsAffected());
        assertThat(policy.getLastExecutedAt()).isNotNull();

        verify(repository).save(policy);
        verify(executionLogRepository).save(any(DataRetentionExecutionLog.class));
    }
}
