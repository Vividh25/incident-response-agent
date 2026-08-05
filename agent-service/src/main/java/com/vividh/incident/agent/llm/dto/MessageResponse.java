package com.vividh.incident.agent.llm.dto;

import lombok.Data;

import java.util.List;

@Data
public class MessageResponse {
    String id;
    String type;
    String role;
    String model;
    List<Content> content;
    Usage usage;
}
