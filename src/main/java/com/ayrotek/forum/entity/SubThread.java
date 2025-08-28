package com.ayrotek.forum.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Data @Getter @Setter @NoArgsConstructor

@Table(name = "subthreads")
public class SubThread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false) private String userId;
    @Column(nullable=false) private String title;
    @Column(columnDefinition="text") private String content;
    /*@Column(nullable=false) private boolean locked = false;*/
    @Column(nullable=false) private Instant createdAt = Instant.now();
    /*@Column private Instant updatedAt;*/



    @ManyToOne
    @JoinColumn(name = "thread_id", nullable = false)
    private Thread thread;
}
