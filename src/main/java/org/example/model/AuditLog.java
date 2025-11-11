package org.example.model;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class AuditLog {
    private String id;
    private String username;
    private String action;
    private String details;
    private LocalDateTime timestamp;

    public AuditLog(String username, String action, String details) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.action = action;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s - %s",
                timestamp, username, action, details);
    }
}
