package com.ayrotek.forum.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ayrotek.forum.repo.MessageRepo;
import com.ayrotek.forum.entity.Message;
import com.ayrotek.forum.entity.SubThread;

@Service

public class MessageService {

    private final MessageRepo messageRepo;

    @Autowired
    public MessageService(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }


    public List<Message> getAllMessagesBySubThreadId(Long subThreadId) {
        return messageRepo.findBySubThreadIdOrderByUpvoteCountDesc(subThreadId);
    }

    public Message getMessageById(Long id) {
        return messageRepo.findById(id).orElse(null);
    }

    public Message createMessage(Message message) {
        message.setBody(message.getBody());
        message.setCreatedAt(message.getCreatedAt());
        message.setUserId(message.getUserId());
        message.setSubThread(message.getSubThread());

        return messageRepo.save(message);
    }

    public void deleteMessage(Long id) {
        messageRepo.deleteById(id);
    }

    public Message updateMessage(Long id, Message updatedMessage) {
        return messageRepo.findById(id).map(message -> {
            message.setBody(updatedMessage.getBody());
            message.setUpdatedAt(java.time.Instant.now());
            return messageRepo.save(message);
        }).orElse(null);
    }

}
