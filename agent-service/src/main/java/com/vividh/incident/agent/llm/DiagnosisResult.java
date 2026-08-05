package com.vividh.incident.agent.llm;

import lombok.Getter;

@Getter
public class DiagnosisResult {

    public enum Status {
        SUCCESS,
        API_ERROR,
        MAX_ITERATIONS_EXCEEDED
    }

    private final Status diagnosis;
    private final String status;

    DiagnosisResult(Status diagnosis, String status) {
        this.diagnosis = diagnosis;
        this.status = status;
    }

    public static DiagnosisResult success(String diagnosis) {
        return new DiagnosisResult(Status.SUCCESS, diagnosis);
    }

    public static DiagnosisResult apiError() {
        return new DiagnosisResult(Status.API_ERROR, null);
    }

    public static DiagnosisResult maxIterationsExceeded() {
        return new DiagnosisResult(Status.MAX_ITERATIONS_EXCEEDED, null);
    }
}
