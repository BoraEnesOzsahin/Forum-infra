package com.ayrotek.forum.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;

@Entity
@Data
public class Post {
    @Id
    private Long id;


    private String message_body;

    // Stores the user id (from JWT sub) who created the post
    private String userId;

    private String created_at;

    private String edited_at;

    private boolean is_deleted;

    @ManyToOne
    @JoinColumn(name = "thread_id", referencedColumnName = "id", nullable = false)
    private Thread thread;
}
