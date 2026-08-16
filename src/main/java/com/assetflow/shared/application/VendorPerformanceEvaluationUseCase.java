package com.assetflow.shared.application;

import com.assetflow.shared.domain.VendorPerformanceEvaluation;
import com.assetflow.shared.domain.VendorPerformanceEvaluationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class VendorPerformanceEvaluationUseCase {

    private static final Logger log = LoggerFactory.getLogger(VendorPerformanceEvaluationUseCase.class);

    private final VendorPerformanceEvaluationRepository repository;

    public VendorPerformanceEvaluationUseCase(VendorPerformanceEvaluationRepository repository) {
        this.repository = repository;
    }

    public record VendorPerformanceSummary(
            UUID organizationId,
            UUID vendorId,
            int totalEvaluations,
            BigDecimal averageDeliveryScore,
            BigDecimal averageQualityScore,
            BigDecimal averageCommunicationScore,
            BigDecimal overallRating
    ) {}

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
        log.info("Recorded performance evaluation for vendor {} in org {} (Overall Rating: {})",
                vendorId, organizationId, eval.getOverallRating());
        return repository.save(eval);
    }

    @Transactional(readOnly = true)
    public VendorPerformanceSummary getVendorPerformanceSummary(UUID organizationId, UUID vendorId) {
        List<VendorPerformanceEvaluation> list = repository.findByOrganizationIdAndVendorId(organizationId, vendorId);
        if (list.isEmpty()) {
            return new VendorPerformanceSummary(organizationId, vendorId, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        int count = list.size();
        double avgDelivery = list.stream().mapToInt(VendorPerformanceEvaluation::getDeliveryScore).average().orElse(0.0);
        double avgQuality = list.stream().mapToInt(VendorPerformanceEvaluation::getQualityScore).average().orElse(0.0);
        double avgComm = list.stream().mapToInt(VendorPerformanceEvaluation::getCommunicationScore).average().orElse(0.0);

        BigDecimal delScore = BigDecimal.valueOf(avgDelivery).setScale(2, RoundingMode.HALF_UP);
        BigDecimal qualScore = BigDecimal.valueOf(avgQuality).setScale(2, RoundingMode.HALF_UP);
        BigDecimal commScore = BigDecimal.valueOf(avgComm).setScale(2, RoundingMode.HALF_UP);

        BigDecimal overallSum = list.stream()
                .map(VendorPerformanceEvaluation::getOverallRating)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal overallRating = overallSum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);

        return new VendorPerformanceSummary(
                organizationId,
                vendorId,
                count,
                delScore,
                qualScore,
                commScore,
                overallRating
        );
    }

    @Transactional(readOnly = true)
    public List<VendorPerformanceEvaluation> getEvaluationsForVendor(UUID organizationId, UUID vendorId) {
        return repository.findByOrganizationIdAndVendorId(organizationId, vendorId);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateAverageVendorRating(UUID organizationId, UUID vendorId) {
        return getVendorPerformanceSummary(organizationId, vendorId).overallRating();
    }
}
