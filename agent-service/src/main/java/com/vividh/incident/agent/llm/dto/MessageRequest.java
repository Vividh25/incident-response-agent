package com.vividh.incident.agent.llm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MessageRequest {
    String model;
    @JsonProperty("max_tokens")
    int maxTokens;
    List<Message> messages;
    List<Tool> tools;
    Thinking thinking;

    public MessageRequest(String model, int maxTokens, List<Message> messages, List<Tool> tools, Thinking thinking) {
        this.model = model;
        this.maxTokens = maxTokens;
        this.messages = messages;
        this.tools = tools;
        this.thinking = thinking;
    }
}
