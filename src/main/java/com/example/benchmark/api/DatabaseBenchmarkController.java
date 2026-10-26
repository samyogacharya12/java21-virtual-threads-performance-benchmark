package com.example.benchmark.api;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/benchmark")
public class DatabaseBenchmarkController {


    private final JdbcTemplate jdbcTemplate;

    public DatabaseBenchmarkController(
            JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/database-load")
    public Map<String, Object> databaseLoad(
            @RequestParam int delayMs) {

        if (delayMs < 0 || delayMs > 5000) {
            throw new IllegalArgumentException(
                    "delayMs must be between 0 and 5000");
        }

        long start = System.nanoTime();

        // PostgreSQL waits while holding a JDBC connection.
        jdbcTemplate.queryForObject(
                "SELECT pg_sleep(? / 1000.0)",
                (rs, rowNum) -> rs.getObject(1),
                delayMs
        );

        long durationMs =
                (System.nanoTime() - start) / 1_000_000;

        return Map.of(
                "executionTimeMs", durationMs,
                "virtualThread",
                Thread.currentThread().isVirtual(),
                "delayMs", delayMs
        );
    }

}
