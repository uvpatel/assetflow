package com.assetflow.audit.api;

import com.assetflow.audit.api.dto.CreateAuditCycleDto;
import com.assetflow.audit.api.dto.VerifyAuditItemDto;
import com.assetflow.audit.application.AuditUseCase;
import com.assetflow.audit.domain.AuditCycle;
import com.assetflow.audit.domain.AuditItem;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/audits")
public class AuditController {

    private final AuditUseCase auditUseCase;

    public AuditController(AuditUseCase auditUseCase) {
        this.auditUseCase = auditUseCase;
    }

    @PostMapping
    public ResponseEntity<AuditCycle> createAuditCycle(@Valid @RequestBody CreateAuditCycleDto dto) {
        AuditCycle cycle = auditUseCase.createAuditCycle(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cycle);
    }

    @PostMapping("/items/{itemId}/verify")
    public ResponseEntity<AuditItem> verifyItem(
            @PathVariable UUID itemId,
            @Valid @RequestBody VerifyAuditItemDto dto) {
        AuditItem item = auditUseCase.verifyItem(itemId, dto);
        return ResponseEntity.ok(item);
    }

    @PostMapping("/{cycleId}/close")
    public ResponseEntity<AuditCycle> closeCycle(@PathVariable UUID cycleId) {
        AuditCycle cycle = auditUseCase.closeCycle(cycleId);
        return ResponseEntity.ok(cycle);
    }

    @GetMapping
    public ResponseEntity<List<AuditCycle>> listCycles() {
        return ResponseEntity.ok(auditUseCase.listCycles());
    }

    @GetMapping("/{cycleId}/items")
    public ResponseEntity<List<AuditItem>> listItems(@PathVariable UUID cycleId) {
        return ResponseEntity.ok(auditUseCase.listItems(cycleId));
    }
}
