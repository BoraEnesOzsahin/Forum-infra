package com.ayrotek.forum.dto;

import java.time.Instant;

public class SubThreadDto {
    private Long id;
    private Long threadId;
    private Long userId;
    private String title;
    private String username;
    /*private String content;*/
    private Instant createdAt;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getThreadId() { return threadId; }
    public void setThreadId(Long threadId) { this.threadId = threadId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    /*public String getContent() { return content; }
    public void setContent(String content) { this.content = content; } */
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
