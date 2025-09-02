

package com.ayrotek.forum.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.PrePersist;


@Entity @Table(name = "users")
@Getter @Setter @NoArgsConstructor



public class User {
    @Id @GeneratedValue private Long id;
    @Column(nullable=false, unique=true) private String username;
    @Column(nullable=false) private String message;
    @Column(nullable=false, updatable=false)
    private java.time.Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.Instant.now();
    }
    @Column(nullable=false) private String model_id;
    @Column(nullable=false) private Role role; // user, mod, admin

    
    public enum Role {
        ADMIN,
        REGULAR
    }

}
