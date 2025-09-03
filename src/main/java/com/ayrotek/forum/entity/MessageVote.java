package com.ayrotek.forum.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name="message_vote")
@Data
@NoArgsConstructor
@IdClass(MessageVote.PK.class)
public class MessageVote {
    
    @Id
    @Column(name="message_id", nullable=false)
    private Long messageId;

    @Id
    @Column(name="user_id", nullable=false)
    private String userId;

    @Column(nullable=false)
    private boolean upvoted; // true for upvote, false for downvote

    @Column(nullable=false, updatable=false)
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.Instant.now();
    }



    /*@ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;*/

    // Composite primary key class
    public static class PK implements Serializable {
        private Long messageId;
        private String userId;

        public PK() {}

        public PK(Long messageId, String userId) {
            this.messageId = messageId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PK)) return false;
            PK pk = (PK) o;
            return messageId.equals(pk.messageId) && userId.equals(pk.userId);
        }

        @Override
        public int hashCode() {
            return 31 * messageId.hashCode() + userId.hashCode();
        }


    }
}
