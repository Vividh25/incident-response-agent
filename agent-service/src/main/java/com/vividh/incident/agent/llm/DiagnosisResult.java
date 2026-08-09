package com.vividh.incident.agent.llm;

import com.vividh.incident.agent.llm.dto.RemediationProposal;
import lombok.Getter;

@Getter
public class DiagnosisResult {

    public enum Status {
        SUCCESS,
        API_ERROR,
        MAX_ITERATIONS_EXCEEDED,
        REMEDIATION_PROPOSAL
    }

    private final String diagnosis;
    private final Status status;
    private final RemediationProposal proposal;

    DiagnosisResult(String diagnosis, Status status, RemediationProposal proposal) {
        this.diagnosis = diagnosis;
        this.status = status;
        this.proposal = proposal;
    }

    public static DiagnosisResult success(String diagnosis) {
        return new DiagnosisResult(diagnosis, Status.SUCCESS, null);
    }

    public static DiagnosisResult apiError() {
        return new DiagnosisResult(null, Status.API_ERROR,null);
    }

    public static DiagnosisResult maxIterationsExceeded() {
        return new DiagnosisResult(null, Status.MAX_ITERATIONS_EXCEEDED, null);
    }

    public static DiagnosisResult remediationProposal(RemediationProposal proposal) {
        return new DiagnosisResult(null, Status.REMEDIATION_PROPOSAL, proposal);
    }
}
