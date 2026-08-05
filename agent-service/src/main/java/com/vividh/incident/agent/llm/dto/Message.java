package com.vividh.incident.agent.llm.dto;

import lombok.Data;

/**
 * content is either a plain String (simple text turn) or a List of content
 * blocks (e.g. List&lt;Content&gt; to echo an assistant's tool_use turn back,
 * or List&lt;ToolResultBlock&gt; to report a tool's result) — matching the
 * Anthropic API's content union. Jackson serializes whichever is actually set.
 */
@Data
public class Message {
    String role;
    Object content;

    public Message(String role, Object content) {
        this.role = role;
        this.content = content;
    }
}
