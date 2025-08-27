package com.ayrotek.forum.dto;

import java.util.List;

import lombok.Data;

@Data
public class ThreadDto {
    private Long id;
    private String title;
    private String content;
    private List<String> tags;
    private List<PostDto> message;
}
