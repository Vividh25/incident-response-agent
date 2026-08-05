package com.vividh.incident.agent.llm.dto;

import lombok.Data;

@Data
public class Thinking {

    String type;

    public Thinking(String type) {
        this.type = type;
    }
}
