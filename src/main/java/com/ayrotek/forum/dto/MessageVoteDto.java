package com.ayrotek.forum.dto;

import java.time.Instant;

public class MessageVoteDto {
    private Long messageId;
    private Long userId;
    private boolean upvoted;
    private Instant createdAt;
    private Instant updatedAt;

    // Getters and setters
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public boolean isUpvoted() { return upvoted; }
    public void setUpvoted(boolean upvoted) { this.upvoted = upvoted; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
