package com.vividh.incident.agent.llm;

import com.vividh.incident.agent.diagnosis.RemediationProposal;
import com.vividh.incident.agent.llm.dto.Tool;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class ToolDefinitions {

    public static final Tool GET_SERVICE_HEALTH = new Tool(
            "get_service_health",
            "Get the current health status and recent error counts for a named service.",
            Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "service_name", Map.of(
                                    "type", "string",
                                    "description", "The name of the service to check, e.g. toy-target-app"
                            )
                    ),
                    "required", List.of("service_name")
            )
    );

    public static final Tool PROPOSE_REMEDIATION = new Tool(
            "propose_remediation",
            "Suggest an action based on the error and wait for human approval",
            Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "service_name", Map.of(
                                    "type", "string",
                                    "description", "The name of the service to check"
                            ),
                            "action", Map.of(
                                    "type", "string",
                                    "enum", Arrays.stream(RemediationProposal.Action.values())
                                            .map(Enum::name)
                                            .toList(),
                                    "description", "The remediation action to take"
                            ),
                            "reasoning", Map.of(
                                    "type", "string",
                                    "description", "Justification for why this action addresses the alert"
                            )
                    ),
                    "required", List.of("service_name", "action", "reasoning")
            )
    );

    private ToolDefinitions() {
    }
}