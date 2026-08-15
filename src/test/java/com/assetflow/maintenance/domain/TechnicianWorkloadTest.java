package com.assetflow.maintenance.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TechnicianWorkloadTest {

    @Test
    void shouldAssignAndCompleteTasksCorrectly() {
        UUID orgId = UUID.randomUUID();
        UUID techId = UUID.randomUUID();

        TechnicianWorkload workload = TechnicianWorkload.register(
                orgId,
                techId,
                2,
                "Electrical & HVAC Systems"
        );

        assertThat(workload.getActiveTaskCount()).isEqualTo(0);
        assertThat(workload.isAvailable()).isTrue();

        workload.assignTask();
        assertThat(workload.getActiveTaskCount()).isEqualTo(1);
        assertThat(workload.isAvailable()).isTrue();

        workload.assignTask();
        assertThat(workload.getActiveTaskCount()).isEqualTo(2);
        assertThat(workload.isAvailable()).isFalse();

        workload.completeTask();
        assertThat(workload.getActiveTaskCount()).isEqualTo(1);
        assertThat(workload.isAvailable()).isTrue();
    }

    @Test
    void shouldRejectTaskAssignmentWhenAtFullCapacity() {
        UUID orgId = UUID.randomUUID();
        UUID techId = UUID.randomUUID();

        TechnicianWorkload workload = TechnicianWorkload.register(orgId, techId, 1, "IT Repairs");
        workload.assignTask();

        assertThatThrownBy(workload::assignTask)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("maximum task capacity");
    }
}
