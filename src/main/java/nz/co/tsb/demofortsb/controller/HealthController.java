package nz.co.tsb.demofortsb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", "DemoForTSB");
        health.put("timestamp", LocalDateTime.now());
        health.put("version", "1.0.0");
        health.put("java_version", System.getProperty("java.version"));
        return health;
    }

    @GetMapping("/")
    public Map<String, String> welcome() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to DemoForTSB Banking API");
        response.put("documentation", "/swagger-ui.html (will be available after Stage 2)");
        return response;
    }
}
