package com.vividh.incident.toyapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // needed so HealthMonitor's @Scheduled method actually fires
public class ToyTargetAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(ToyTargetAppApplication.class, args);
    }
}
