package com.ayrotek.forum.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ayrotek.forum.repo.MessageRepo;
import com.ayrotek.forum.entity.Message;
import com.ayrotek.forum.entity.SubThread;
import com.ayrotek.forum.service.UserService;

@Service

public class MessageService {

    private final MessageRepo messageRepo;
    private final UserService userService;

    public MessageService(MessageRepo messageRepo, UserService userService) {
        this.messageRepo = messageRepo;
        this.userService = userService;
    }


    public List<Message> getAllMessagesBySubThreadId(Long subThreadId) {
        return messageRepo.findBySubThreadIdOrderByUpvoteCountDesc(subThreadId);
    }

    public Message getMessageById(Long id) {
        return messageRepo.findById(id).orElse(null);
    }

    public Message createMessage(Message message) {
        // Ensure user exists before creating message
        // Fetch model_id from the grandparent thread to enforce the NOT NULL constraint
        String modelId = message.getSubThread().getThread().getModelId();
        userService.ensureUserExists(message.getUserId(), modelId);
        
        Message savedMessage = messageRepo.save(message);
        userService.addMessageToUser(savedMessage.getUserId(), savedMessage.getBody());

        return savedMessage;
    }

    public void deleteMessage(Long id) {
        messageRepo.deleteById(id);
    }

    /*public Message updateMessage(Long id, Message updatedMessage) {
        return messageRepo.findById(id).map(message -> {
            message.setBody(updatedMessage.getBody());
            message.setUpdatedAt(java.time.Instant.now());
            return messageRepo.save(message);
        }).orElse(null);
    }*/

}
