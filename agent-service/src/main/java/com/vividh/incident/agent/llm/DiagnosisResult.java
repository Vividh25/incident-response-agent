package com.vividh.incident.agent.llm;

import lombok.Getter;

@Getter
public class DiagnosisResult {

    public enum Status {
        SUCCESS,
        API_ERROR,
        MAX_ITERATIONS_EXCEEDED
    }

    private final String diagnosis;
    private final Status status;

    DiagnosisResult(String diagnosis, Status status) {
        this.diagnosis = diagnosis;
        this.status = status;
    }

    public static DiagnosisResult success(String diagnosis) {
        return new DiagnosisResult(diagnosis, Status.SUCCESS);
    }

    public static DiagnosisResult apiError() {
        return new DiagnosisResult(null, Status.API_ERROR);
    }

    public static DiagnosisResult maxIterationsExceeded() {
        return new DiagnosisResult(null, Status.MAX_ITERATIONS_EXCEEDED);
    }
}
