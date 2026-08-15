package com.assetflow.asset.api;

import com.assetflow.asset.application.AssetConditionAssessmentUseCase;
import com.assetflow.asset.domain.AssetConditionAssessment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/assets/{assetId}/condition-assessments")
public class AssetConditionAssessmentController {

    private final AssetConditionAssessmentUseCase useCase;

    public AssetConditionAssessmentController(AssetConditionAssessmentUseCase useCase) {
        this.useCase = useCase;
    }

    public record RecordAssessmentRequest(
            UUID assessorEmployeeId,
            int conditionScore,
            String assessmentNotes
    ) {}

    public record AssessmentResponse(
            UUID id,
            UUID organizationId,
            UUID assetId,
            UUID assessorEmployeeId,
            int conditionScore,
            String assessmentNotes,
            Instant assessedAt
    ) {}

    @PostMapping
    public ResponseEntity<AssessmentResponse> recordAssessment(
            @PathVariable UUID organizationId,
            @PathVariable UUID assetId,
            @RequestBody RecordAssessmentRequest request) {
        AssetConditionAssessment assessment = useCase.recordAssessment(
                organizationId,
                assetId,
                request.assessorEmployeeId(),
                request.conditionScore(),
                request.assessmentNotes()
        );
        return ResponseEntity.ok(toResponse(assessment));
    }

    @GetMapping
    public ResponseEntity<List<AssessmentResponse>> getAssessmentHistory(
            @PathVariable UUID organizationId,
            @PathVariable UUID assetId) {
        List<AssessmentResponse> list = useCase.getAssessmentHistory(organizationId, assetId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    private AssessmentResponse toResponse(AssetConditionAssessment a) {
        return new AssessmentResponse(
                a.getId(),
                a.getOrganizationId(),
                a.getAssetId(),
                a.getAssessorEmployeeId(),
                a.getConditionScore(),
                a.getAssessmentNotes(),
                a.getAssessedAt()
        );
    }
}
