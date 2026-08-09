package com.vividh.incident.agent.llm;

import com.vividh.incident.agent.event.AlertEvent;
import com.vividh.incident.agent.kafka.AlertListener;
import com.vividh.incident.agent.llm.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DiagnosticAgent {

    @Value("${diagnostic-agent.max-iterations}")
    private int MAX_ITERATIONS;

    private final ClaudeService claudeService;

    public DiagnosisResult diagnose(AlertEvent event) {
        List<Message> messages = new ArrayList<>();
        String prompt = buildPrompt(event);
        messages.add(new Message("user", prompt));

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            MessageResponse response = claudeService.send(messages);

            if (response == null) return DiagnosisResult.apiError();

            Optional<Content> toolUseBlock = response.getContent()
                    .stream()
                    .filter(block -> "tool_use".equals(block.getType()))
                    .findFirst();

            if (toolUseBlock.isEmpty()) {
                return response.getContent()
                        .stream().filter(block -> "text".equals(block.getType()))
                        .findFirst()
                        .map(Content::getText)
                        .map(DiagnosisResult::success)
                        .orElseGet(DiagnosisResult::apiError);
            }

            messages.add(new Message("assistant", response.getContent()));

            Content block = toolUseBlock.get();
            String toolName = block.getName();
            Map<String, Object> toolInput = block.getInput();

            if (toolName.equals("propose_remediation")) {
                String serviceName = (String) toolInput.get("service_name");
                String reasoning = (String) toolInput.get("reasoning");
                String action = (String) toolInput.get("action");
                RemediationProposal.Action parsedAction = RemediationProposal.Action.valueOf(action);
                RemediationProposal proposal = RemediationProposal.of(parsedAction, serviceName, reasoning);

                return DiagnosisResult.remediationProposal(proposal);

            }
            String toolUseId = block.getId();

            String toolResult = executeTool(toolName, toolInput);
            messages.add(new Message("user", List.of(new ToolResultBlock(toolUseId, toolResult))));
        }

        return DiagnosisResult.maxIterationsExceeded();
    }

    private String buildPrompt(AlertEvent event) {
        String start = "An alert fired: ";
        String end = "Investigate the likely cause using available tools.";

        String source = "Source: " + event.source();
        String metric = "Metric: " + event.metric();
        String value = "Value: " + event.value();
        String threshold = "Threshold: " + event.threshold();
        String remediate = "Proposing a remediation is an available next step.";

        return start + source + ", " + metric + ", " + value + ", " + threshold + ". " + end + System.lineSeparator() + remediate;
    }

    private String executeTool(String toolName, Map<String, Object> toolInput) {
        if (toolName.equals("get_service_health")) {
            return "Service Name: " + (String) toolInput.get("service_name") + " " + "error_rate: 0.4, status: Network error";
        }
        return "No tool found by the name: " + toolName;
    }
}
