package com.vividh.incident.agent.diagnosis;

import com.vividh.incident.agent.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ApprovalStore {

    private final RemediationExecutor remediationExecutor;

    public enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    public record Approval(String serviceName, RemediationProposal.Action action, String reasoning, ApprovalStatus status){}

    private final ConcurrentHashMap<UUID, Approval> map = new ConcurrentHashMap<>();

    public UUID addApproval(RemediationProposal proposal) {
        Approval newApproval = new Approval(proposal.getServiceName(), proposal.getAction(), proposal.getReasoning(), ApprovalStatus.PENDING);
        UUID id = UUID.randomUUID();
        map.put(id, newApproval);
        return id;
    }

    public Approval updateStatus(UUID id, ApprovalStatus newStatus) {
        // compute() makes the read-modify-write atomic per key, unlike a
        // separate get()+put() - important once this is called concurrently
        // from multiple REST requests approving/rejecting the same id.
        Approval updatedApproval = map.compute(id, (key, currentApproval) -> {
            if (currentApproval == null) {
                throw new ResourceNotFoundException("No approval found with the id: " + id);
            }
            return new Approval(currentApproval.serviceName(), currentApproval.action(), currentApproval.reasoning(), newStatus);
        });

        if (newStatus == ApprovalStatus.APPROVED) {
            remediationExecutor.execute(updatedApproval);
        }

        return updatedApproval;
    }

    public List<Approval> listPendingApprovals() {
        return map.values()
                .stream()
                .filter(approval -> approval.status == ApprovalStatus.PENDING)
                .toList();
    }

    public Approval getApprovalById(UUID id) {
        Approval approval = map.get(id);
        if (approval == null) {
            throw new ResourceNotFoundException("No approval found with id: " + id);
        }
        return approval;
    }

}
