package com.ayrotek.forum.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "message_vote")
@Data
public class MessageVote {

    @EmbeddedId
    private PK id;

    @Column(name = "upvoted", nullable = false)
    private boolean upvoted;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    // Convenience getters/setters
    public Long getMessageId() {
        return id != null ? id.getMessageId() : null;
    }
    public Long getUserId() {
        return id != null ? id.getUserId() : null;
    }
    public void setMessageId(Long messageId) {
        if (id == null) id = new PK();
        id.setMessageId(messageId);
    }
    public void setUserId(Long userId) {
        if (id == null) id = new PK();
        id.setUserId(userId);
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class PK implements Serializable {
        @Column(name = "message_id")
        private Long messageId;

        @Column(name = "user_id")
        private Long userId;
    }
}
