package com.ayrotek.forum.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "thread_tags", joinColumns = @JoinColumn(name = "thread_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    public enum VehicleType {
        COMMERCIAL, PERSONAL
    }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags != null ? tags : new HashSet<>(); }
}
