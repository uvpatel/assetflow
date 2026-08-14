package com.assetflow.allocation.application;

import com.assetflow.allocation.domain.AssetAllocation;
import com.assetflow.allocation.domain.AssetAllocationRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf.Logger;
import org.slf.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class OverdueJobScheduler {

    private static final Logger log = LoggerFactory.getLogger(OverdueJobScheduler.class);

    private final AssetAllocationRepository allocationRepository;

    public OverdueJobScheduler(AssetAllocationRepository allocationRepository) {
        this.allocationRepository = allocationRepository;
    }

    @Scheduled(cron = "${assetflow.jobs.overdue-check.cron:0 0 * * * *}")
    @SchedulerLock(name = "OverdueJobScheduler_checkOverdueAllocations", lockAtLeastFor = "1m", lockAtMostFor = "15m")
    public void checkOverdueAllocations() {
        log.info("Starting scheduled overdue allocation check...");
        List<AssetAllocation> overdueAllocations = allocationRepository.findOverdueAllocations(Instant.now());
        log.info("Found {} overdue asset allocations.", overdueAllocations.size());
        for (AssetAllocation allocation : overdueAllocations) {
            log.warn("Allocation {} for asset {} (Org: {}) is overdue. Expected return was {}.",
                    allocation.getId(), allocation.getAssetId(), allocation.getOrganizationId(), allocation.getExpectedReturnAt());
        }
        log.info("Completed scheduled overdue allocation check.");
    }
}
