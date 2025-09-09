package com.ayrotek.forum.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Entity
@Data
@Table(name = "threads")
@NoArgsConstructor
public class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable=false) private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false) private VehicleType type; // Commercial or Personal
    @Column(name = "model_id", nullable=false) private String modelId;
    @Column(nullable=false) private String title;
    /*@Column(nullable=false) private boolean locked = false;*/

    @Column(name = "created_at", nullable=false, updatable=false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
    /*private Instant updatedAt;*/

    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubThread> subThreads;

    public enum VehicleType {
        COMMERCIAL, PERSONAL
    }
}
