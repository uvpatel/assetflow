package com.assetflow.shared.api;

import com.assetflow.shared.application.VendorPerformanceEvaluationUseCase;
import com.assetflow.shared.domain.VendorPerformanceEvaluation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/vendors/{vendorId}/evaluations")
public class VendorPerformanceEvaluationController {

    private final VendorPerformanceEvaluationUseCase useCase;

    public VendorPerformanceEvaluationController(VendorPerformanceEvaluationUseCase useCase) {
        this.useCase = useCase;
    }

    public record RecordEvaluationRequest(
            UUID evaluatorUserId,
            int deliveryScore,
            int qualityScore,
            int communicationScore,
            String evaluationNotes
    ) {}

    public record EvaluationResponse(
            UUID id,
            UUID organizationId,
            UUID vendorId,
            UUID evaluatorUserId,
            int deliveryScore,
            int qualityScore,
            int communicationScore,
            BigDecimal overallRating,
            String evaluationNotes,
            Instant evaluatedAt
    ) {}

    public record VendorAverageRatingResponse(
            UUID vendorId,
            BigDecimal averageRating,
            int totalEvaluations
    ) {}

    @PostMapping
    public ResponseEntity<EvaluationResponse> recordEvaluation(
            @PathVariable UUID organizationId,
            @PathVariable UUID vendorId,
            @RequestBody RecordEvaluationRequest request) {
        VendorPerformanceEvaluation eval = useCase.recordEvaluation(
                organizationId,
                vendorId,
                request.evaluatorUserId(),
                request.deliveryScore(),
                request.qualityScore(),
                request.communicationScore(),
                request.evaluationNotes()
        );
        return ResponseEntity.ok(toResponse(eval));
    }

    @GetMapping
    public ResponseEntity<List<EvaluationResponse>> getEvaluations(
            @PathVariable UUID organizationId,
            @PathVariable UUID vendorId) {
        List<EvaluationResponse> list = useCase.getEvaluationsForVendor(organizationId, vendorId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/average-rating")
    public ResponseEntity<VendorAverageRatingResponse> getAverageRating(
            @PathVariable UUID organizationId,
            @PathVariable UUID vendorId) {
        List<VendorPerformanceEvaluation> list = useCase.getEvaluationsForVendor(organizationId, vendorId);
        BigDecimal avg = useCase.calculateAverageVendorRating(organizationId, vendorId);
        return ResponseEntity.ok(new VendorAverageRatingResponse(vendorId, avg, list.size()));
    }

    private EvaluationResponse toResponse(VendorPerformanceEvaluation e) {
        return new EvaluationResponse(
                e.getId(),
                e.getOrganizationId(),
                e.getVendorId(),
                e.getEvaluatorUserId(),
                e.getDeliveryScore(),
                e.getQualityScore(),
                e.getCommunicationScore(),
                e.getOverallRating(),
                e.getEvaluationNotes(),
                e.getEvaluatedAt()
        );
    }
}
