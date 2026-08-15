package com.assetflow.shared.application;

import com.assetflow.shared.domain.VendorPerformanceEvaluation;
import com.assetflow.shared.domain.VendorPerformanceEvaluationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class VendorPerformanceEvaluationUseCase {

    private final VendorPerformanceEvaluationRepository repository;

    public VendorPerformanceEvaluationUseCase(VendorPerformanceEvaluationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public VendorPerformanceEvaluation recordEvaluation(
            UUID organizationId,
            UUID vendorId,
            UUID evaluatorUserId,
            int deliveryScore,
            int qualityScore,
            int communicationScore,
            String evaluationNotes) {
        VendorPerformanceEvaluation eval = VendorPerformanceEvaluation.evaluate(
                organizationId,
                vendorId,
                evaluatorUserId,
                deliveryScore,
                qualityScore,
                communicationScore,
                evaluationNotes
        );
        return repository.save(eval);
    }

    @Transactional(readOnly = true)
    public List<VendorPerformanceEvaluation> getEvaluationsForVendor(UUID organizationId, UUID vendorId) {
        return repository.findByOrganizationIdAndVendorId(organizationId, vendorId);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateAverageVendorRating(UUID organizationId, UUID vendorId) {
        List<VendorPerformanceEvaluation> list = repository.findByOrganizationIdAndVendorId(organizationId, vendorId);
        if (list.isEmpty()) return BigDecimal.ZERO;

        BigDecimal sum = list.stream()
                .map(VendorPerformanceEvaluation::getOverallRating)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(list.size()), 2, RoundingMode.HALF_UP);
    }
}
