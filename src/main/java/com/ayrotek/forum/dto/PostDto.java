package com.ayrotek.forum.dto;

import lombok.Data;

@Data
public class PostDto {
    private Long id;
    private String messageBody;
    private String threadId;
    private String createdAt;
}
