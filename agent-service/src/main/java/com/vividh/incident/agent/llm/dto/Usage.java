package com.vividh.incident.agent.llm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Usage {
    @JsonProperty("input_tokens")
    int inputTokens;
    @JsonProperty("output_tokens")
    int outputTokens;
}
