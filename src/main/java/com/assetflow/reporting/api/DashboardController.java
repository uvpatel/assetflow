package com.assetflow.reporting.api;

import com.assetflow.reporting.api.dto.OrganizationDashboardSummaryDto;
import com.assetflow.reporting.application.DashboardUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardUseCase dashboardUseCase;

    public DashboardController(DashboardUseCase dashboardUseCase) {
        this.dashboardUseCase = dashboardUseCase;
    }

    @GetMapping("/summary")
    public ResponseEntity<OrganizationDashboardSummaryDto> getSummary() {
        return ResponseEntity.ok(dashboardUseCase.getDashboardSummary());
    }
}
