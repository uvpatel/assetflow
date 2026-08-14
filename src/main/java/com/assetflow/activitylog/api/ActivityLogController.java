package com.assetflow.activitylog.api;

import com.assetflow.activitylog.application.ActivityLogUseCase;
import com.assetflow.activitylog.domain.ActivityLog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/activity-logs")
public class ActivityLogController {

    private final ActivityLogUseCase activityLogUseCase;

    public ActivityLogController(ActivityLogUseCase activityLogUseCase) {
        this.activityLogUseCase = activityLogUseCase;
    }

    @GetMapping
    public ResponseEntity<List<ActivityLog>> listLogs() {
        return ResponseEntity.ok(activityLogUseCase.listLogs());
    }
}
