package com.vividh.incident.agent;

import com.vividh.incident.agent.event.AlertEvent;
import com.vividh.incident.agent.llm.ClaudeService;
import com.vividh.incident.agent.llm.DiagnosisResult;
import com.vividh.incident.agent.llm.DiagnosticAgent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@SpringBootApplication
public class AgentServiceApplication {

//    @Autowired
//    ClaudeService claudeService;
//
//    @Autowired
//    DiagnosticAgent diagnosticAgent;
//
//    AlertEvent alertEvent = new AlertEvent(
//            "toy-target-app",
//            "critical",
//            "error_rate",
//            0.42,
//            0.05,
//            Instant.now(),
//            Map.of("endpoint", "/api/orders", "instance", "toy-target-app-1")
//    );

    public static void main(String[] args) {
        SpringApplication.run(AgentServiceApplication.class, args);
    }

    // Next session: a Kafka listener consuming AlertEvent from
    // "incident-alerts", then the ReAct diagnostic loop, then the
    // human-in-the-loop approval endpoint. Building those together so each
    // piece comes with an explanation of the pattern it demonstrates,
    // rather than landing as a wall of code with no context.
}
