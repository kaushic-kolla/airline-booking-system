package com.airline.booking.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DatabaseController {
    private final JdbcTemplate jdbcTemplate;
    public DatabaseController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/database/check")
    public Map<String, String> checkDatabase() {

        String databaseName = jdbcTemplate.queryForObject(
                "SELECT current_database()", String.class
        );

        String databaseUser = jdbcTemplate.queryForObject(
                "SELECT current_user", String.class
        );

        return Map.of(
                "status", "CONNECTED",
                "database", databaseName,
                "user", databaseUser
        );

    }

}

