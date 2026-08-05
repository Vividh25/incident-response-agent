package com.vividh.incident.agent.llm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class Tool {

    String name;
    String description;
    @JsonProperty("input_schema")
    Map<String, Object> inputSchema;

    public Tool(String name, String description, Map<String, Object> inputSchema) {
        this.name = name;
        this.description = description;
        this.inputSchema = inputSchema;
    }
}

