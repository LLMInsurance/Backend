package com.example.LLMInsurance_Backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "service", "LLM Insurance Backend"
        );
    }

    @GetMapping("/")
    public Map<String, String> root() {
        return Map.of("message", "LLM Insurance Backend is running!");
    }
} 