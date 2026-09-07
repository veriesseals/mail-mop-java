package com.base256.spamreaper.shared.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// First real endpoint. Public (see SecurityConfig). Used by uptime
// checks / load balancers and by the frontend to confirm the API is
// reachable before it renders.
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public HealthResponse health() {
        return new HealthResponse("UP", "spam-reaper");
    }

    public record HealthResponse(String status, String service) {}
}
