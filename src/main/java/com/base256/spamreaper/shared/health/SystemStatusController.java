package com.base256.spamreaper.shared.health;

import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.Optional;

// Reports when the running process started and which build it is, so the
// frontend can show "backend up since ..." and the correct version in its
// footer. Public, same as /api/health.
//
// BuildProperties is injected as Optional: the spring-boot-maven-plugin
// build-info goal writes META-INF/build-info.properties during the build,
// but it is absent when tests boot the context — fall back to "dev" there
// instead of failing startup.
@RestController
public class SystemStatusController {

    private static final Instant STARTED_AT =
            Instant.ofEpochMilli(ManagementFactory.getRuntimeMXBean().getStartTime());

    private final String version;

    public SystemStatusController(Optional<BuildProperties> buildProperties) {
        this.version = buildProperties.map(BuildProperties::getVersion).orElse("dev");
    }

    @GetMapping("/api/system/status")
    public SystemStatusResponse status() {
        return new SystemStatusResponse(STARTED_AT.toString(), version);
    }

    public record SystemStatusResponse(String startedAt, String version) {}
}
