package com.ayrotek.forum.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Data
@Table(name = "threads")
@Getter @Setter @NoArgsConstructor

public class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false) private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false) private VehicleType role; // Commercial or Personal
    @Column(nullable=false) private String modelId;
    @Column(nullable=false) private String title;
    /*@Column(nullable=false) private boolean locked = false;*/

    @Column(nullable=false) private Instant createdAt = Instant.now();
    /*private Instant updatedAt;*/

    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubThread> subThreads;

    public enum VehicleType {
        COMMERCIAL, PERSONAL
    }
}
