package com.ayrotek.forum.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "subthreads")
public class SubThread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable=false) 
    private String userId;
    @Column(name = "title", nullable=false) private String title;

    //@Column(name = "content", columnDefinition="text") private String content;
    @Column(name = "created_at", nullable=false, updatable=false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
    /*@Column private Instant updatedAt;*/



    @ManyToOne
    @JoinColumn(name = "thread_id", nullable = false)
    private Thread thread;


    @OneToMany(mappedBy = "subThread", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;
}
