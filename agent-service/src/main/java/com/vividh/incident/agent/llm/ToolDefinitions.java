package com.vividh.incident.agent.llm;

import com.vividh.incident.agent.llm.dto.Tool;

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

    private ToolDefinitions() {
    }
}