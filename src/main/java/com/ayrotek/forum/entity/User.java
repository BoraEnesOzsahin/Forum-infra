package com.ayrotek.forum.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import java.util.Set;
import java.util.HashSet;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY) 
    private Long id;
    @Column(nullable=false, unique=true) private String username;
    @Column private String message;
    @Column(nullable=false) @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private Role role;
    @Column(name = "created_at", nullable=false, updatable=false)
    private java.time.Instant createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_model_ids", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "model_id")
    private Set<String> modelIds = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.Instant.now();
    }

    public enum Role {
        ADMIN,
        CITIZEN
    }

    // Lombok will generate getters and setters

    public Set<String> getModelIds() { return modelIds; }
    public void setModelIds(Set<String> modelIds) { this.modelIds = modelIds != null ? modelIds : new HashSet<>(); }

    // Optional convenience (not persisted)
    @Transient
    public String getModelId() {
        return (modelIds == null || modelIds.isEmpty()) ? null : modelIds.iterator().next();
    }
    @Transient
    public void setModelId(String modelId) {
        if (this.modelIds == null) this.modelIds = new HashSet<>();
        this.modelIds.clear();
        if (modelId != null) this.modelIds.add(modelId);
    }
} 