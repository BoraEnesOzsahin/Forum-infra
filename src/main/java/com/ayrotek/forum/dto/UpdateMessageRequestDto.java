package com.ayrotek.forum.dto;
import com.ayrotek.forum.entity.Message;

public class UpdateMessageRequestDto {
    private Message newBody;
    private String username;

    public Message getNewBody() {
        return newBody;
    }

    public String getUsername() {
        return username;
    }

    public void setNewBody(Message newBody) {
        this.newBody = newBody;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
