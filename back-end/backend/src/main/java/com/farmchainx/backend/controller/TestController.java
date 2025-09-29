package com.farmchainx.backend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "Test Controller");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        response.put("message", "Backend is running properly with open security");
        return response;
    }

    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("received", request);
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        response.put("message", "Echo successful");
        return response;
    }

    @GetMapping("/public")
    public Map<String, Object> publicEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "This is a public endpoint");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return response;
    }
}