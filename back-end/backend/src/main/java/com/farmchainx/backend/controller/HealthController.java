package com.farmchainx.backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class HealthController {
    
    @GetMapping("/health")
    public String healthCheck() {
        return "✅ FarmChainX Backend is running properly on port 8090";
    }
    
    @GetMapping("/test")
    public String testEndpoint() {
        return "✅ Test endpoint is working! Backend connectivity is good.";
    }
}