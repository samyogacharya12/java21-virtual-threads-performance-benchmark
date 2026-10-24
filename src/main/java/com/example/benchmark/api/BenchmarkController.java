package com.example.benchmark.api;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/benchmark")
public class BenchmarkController {
    private final JdbcTemplate jdbcTemplate;
    private final String mode;

    public BenchmarkController(JdbcTemplate jdbcTemplate,
                               @Value("${benchmark.mode:platform}") String mode) {
        this.jdbcTemplate = jdbcTemplate;
        this.mode = mode;
    }

    @GetMapping("/io")
    public Map<String, Object> simulateIo(@RequestParam(defaultValue = "100") int delayMs)
            throws InterruptedException {
        if (delayMs < 0 || delayMs > 5000) {
            throw new IllegalArgumentException("delayMs must be between 0 and 5000");
        }
        Thread.sleep(delayMs); // Simulates waiting for remote I/O; not actual network traffic.
        Thread currentThread = Thread.currentThread();
        boolean isVirtual = currentThread.isVirtual();

        return Map.of(
                "mode", isVirtual ? "virtual" : "platform",
                "virtualThread", isVirtual,
                "threadName", currentThread.getName(),
                "delayMs", delayMs
        );
    }

    @GetMapping("/db")
    public Map<String, Object> database() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM accounts", Integer.class);
        return Map.of("mode", mode, "virtualThread", Thread.currentThread().isVirtual(),
                      "accountCount", count == null ? 0 : count);
    }
}
