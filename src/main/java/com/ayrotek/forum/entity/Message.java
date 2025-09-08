package com.ayrotek.forum.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable=false) 
    private String userId;
    @Column(nullable=false) private String body;
    @Column(name = "created_at", nullable=false, updatable=false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.Instant.now();
    }
    @Column(name = "upvote_count", nullable=false) 
    private int upvoteCount = 0; // cached
    @Column(nullable=false) private boolean deleted = false;

    @Column(name = "updated_at") 
    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name = "subthread_id", nullable = false)
    private SubThread subThread;

    /*@OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageVote> message_votes;*/

}
