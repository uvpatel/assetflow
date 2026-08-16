package com.assetflow.asset.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SoftwareLicenseAllocationRepository {
    SoftwareLicenseAllocation save(SoftwareLicenseAllocation allocation);
    Optional<SoftwareLicenseAllocation> findBySoftwareLicenseIdAndEmployeeId(UUID softwareLicenseId, UUID employeeId);
    List<SoftwareLicenseAllocation> findBySoftwareLicenseId(UUID softwareLicenseId);
    void delete(SoftwareLicenseAllocation allocation);
}
