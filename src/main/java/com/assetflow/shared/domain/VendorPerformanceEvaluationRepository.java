package com.assetflow.shared.domain;

import java.util.List;
import java.util.UUID;

public interface VendorPerformanceEvaluationRepository {
    VendorPerformanceEvaluation save(VendorPerformanceEvaluation evaluation);
    List<VendorPerformanceEvaluation> findByOrganizationIdAndVendorId(UUID organizationId, UUID vendorId);
}
