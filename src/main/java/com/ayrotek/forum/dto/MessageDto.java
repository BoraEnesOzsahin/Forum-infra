package com.ayrotek.forum.dto;

import java.time.Instant;
import java.util.List;

public class MessageDto {
    private Long id;
    private Long subThreadId;
    private Long userId;
    private String username;
    private String body;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer upvoteCount;          // added
    private List<String> voters;          // optional list of voter usernames

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSubThreadId() { return subThreadId; }
    public void setSubThreadId(Long subThreadId) { this.subThreadId = subThreadId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Integer getUpvoteCount() { return upvoteCount; }
    public void setUpvoteCount(Integer upvoteCount) { this.upvoteCount = upvoteCount; }
    public List<String> getVoters() { return voters; }
    public void setVoters(List<String> voters) { this.voters = voters; }
}
