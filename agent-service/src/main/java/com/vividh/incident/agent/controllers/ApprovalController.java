package com.vividh.incident.agent.controllers;

import com.vividh.incident.agent.diagnosis.ApprovalStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/approvals")
public class ApprovalController {
    private final ApprovalStore approvalStore;

    @GetMapping
    public List<ApprovalStore.Approval> listPendingApprovals() {
        return approvalStore.listPendingApprovals();
    }

    @GetMapping("/{id}")
    public ApprovalStore.Approval getApprovalById(@PathVariable UUID id) {
        return approvalStore.getApprovalById(id);
    }

    @PostMapping("/{id}/approve")
    public ApprovalStore.Approval approve(@PathVariable UUID id) {
        return approvalStore.updateStatus(id, ApprovalStore.ApprovalStatus.APPROVED);
    }

    @PostMapping("/{id}/reject")
    public ApprovalStore.Approval reject(@PathVariable UUID id) {
        return approvalStore.updateStatus(id, ApprovalStore.ApprovalStatus.REJECTED);
    }
}
