package com.ayrotek.forum.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.PrePersist;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY) 
    private Long id;
    @Column(nullable=false, unique=true) private String username;
    @Column private String message;
    @Column(name = "model_id") private String modelId;
    @Column(nullable=false) @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private Role role;
    @Column(name = "created_at", nullable=false, updatable=false)
    private java.time.Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.Instant.now();
    }

    public enum Role {
        ADMIN,
        CITIZEN
    }

    // Lombok will generate getters and setters
}
