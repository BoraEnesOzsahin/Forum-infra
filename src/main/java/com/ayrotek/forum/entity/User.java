
package com.ayrotek.forum.entity;

public class User {
    private Long id;
    private String username;
    private String message;
    private String passwordHash;
    private String created_at;

    private Role role;

    
    public enum Role {
        ADMIN,
        REGULAR
    }

    // Getters and Setters
}
