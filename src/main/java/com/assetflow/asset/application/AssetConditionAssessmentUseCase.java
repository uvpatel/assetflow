package com.assetflow.asset.application;

import com.assetflow.asset.domain.AssetConditionAssessment;
import com.assetflow.asset.domain.AssetConditionAssessmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AssetConditionAssessmentUseCase {

    private final AssetConditionAssessmentRepository repository;

    public AssetConditionAssessmentUseCase(AssetConditionAssessmentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AssetConditionAssessment recordAssessment(
            UUID organizationId,
            UUID assetId,
            UUID assessorEmployeeId,
            int conditionScore,
            String assessmentNotes) {
        AssetConditionAssessment assessment = AssetConditionAssessment.assess(
                organizationId,
                assetId,
                assessorEmployeeId,
                conditionScore,
                assessmentNotes
        );
        return repository.save(assessment);
    }

    @Transactional(readOnly = true)
    public List<AssetConditionAssessment> getAssessmentHistory(UUID organizationId, UUID assetId) {
        return repository.findByOrganizationIdAndAssetId(organizationId, assetId);
    }
}
