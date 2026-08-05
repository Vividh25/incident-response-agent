package com.vividh.incident.agent.llm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * A tool_result content block, sent back to Claude in a "user" message after
 * executing a tool it requested via a tool_use block.
 */
@Data
public class ToolResultBlock {

    final String type = "tool_result";

    @JsonProperty("tool_use_id")
    String toolUseId;

    String content;

    @JsonProperty("is_error")
    boolean error;

    public ToolResultBlock(String toolUseId, String content) {
        this(toolUseId, content, false);
    }

    public ToolResultBlock(String toolUseId, String content, boolean error) {
        this.toolUseId = toolUseId;
        this.content = content;
        this.error = error;
    }
}