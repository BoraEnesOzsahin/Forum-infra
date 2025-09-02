package com.ayrotek.forum.dto;

import java.time.Instant;

public class MessageDto {
    private Long id;
    private Long subThreadId;
    private Long userId;
    private String body;
    private Instant createdAt = Instant.now();

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSubThreadId() { return subThreadId; }
    public void setSubThreadId(Long subThreadId) { this.subThreadId = subThreadId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Instant getCreatedAt() { return createdAt; }
}
