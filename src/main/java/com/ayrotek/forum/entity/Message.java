package com.ayrotek.forum.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Data @Getter @Setter @NoArgsConstructor

@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false) private String userId;
    @Column(nullable=false) private String body;
    @Column(nullable=false) private Instant createdAt = Instant.now();
    @Column(nullable=false) private int upvoteCount = 0; // cached
    @Column(nullable=false) private boolean deleted = false;

    @Column private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name = "subthread_id", nullable = false)
    private SubThread subThread;
}
