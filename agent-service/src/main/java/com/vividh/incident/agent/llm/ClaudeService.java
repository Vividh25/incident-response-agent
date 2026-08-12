package com.vividh.incident.agent.llm;

import com.vividh.incident.agent.llm.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Slf4j
@Service
//@RequiredArgsConstructor
public class ClaudeService {

    private static final String MODEL = "claude-sonnet-5";

    private final RestClient anthropicClient;

    public ClaudeService(@Qualifier("anthropicRestClient") RestClient anthropicClient) {
        this.anthropicClient = anthropicClient;
    }

    public String sendMessage(String userPrompt) {
        MessageResponse response = send(List.of(new Message("user", userPrompt)));

        if (response != null && response.getContent() != null && !response.getContent().isEmpty()) {
            return response.getContent().stream()
                    .filter(block -> "text".equals(block.getType()))
                    .findFirst()
                    .map(Content::getText)
                    .orElse("No response found");
        }

        return "No response text found";
    }

    /**
     * Sends a full conversation turn and returns the raw response. Use this
     * (instead of {@link #sendMessage}) once the conversation needs more than
     * a single plain-text user turn — e.g. to continue after a tool call:
     * <pre>
     * List&lt;Message&gt; history = new ArrayList&lt;&gt;(previousMessages);
     * history.add(new Message("assistant", priorResponse.getContent())); // echo the tool_use turn
     * history.add(new Message("user", List.of(
     *         new ToolResultBlock(toolUseId, toolOutput))));
     * MessageResponse response = claudeService.send(history);
     * </pre>
     */
    public MessageResponse send(List<Message> messages) {
        MessageRequest requestBody = new MessageRequest(
                MODEL,
                1024,
                messages,
                List.of(ToolDefinitions.GET_SERVICE_HEALTH, ToolDefinitions.PROPOSE_REMEDIATION),
                new Thinking("disabled")
        );

        try {
            return anthropicClient.post()
                    .uri("/v1/messages")
                    .body(requestBody)
                    .retrieve()
                    .body(MessageResponse.class);
        } catch (RestClientResponseException e) {
            log.error("Claude API request failed: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        }
    }
}
