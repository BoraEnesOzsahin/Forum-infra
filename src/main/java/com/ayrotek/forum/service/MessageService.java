package com.ayrotek.forum.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ayrotek.forum.repo.MessageRepo;
import com.ayrotek.forum.entity.Message;
import com.ayrotek.forum.entity.SubThread;
import com.ayrotek.forum.service.UserService;
import com.ayrotek.forum.entity.User;

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

    public Message createMessage(Message message, String username) {

        //if user is admin bypass model_id check
        User requestingUser = userService.getUserByUsername(username);
        String model_id = null;

        if(requestingUser.getRole().toString().equals("ADMIN")){
            model_id = null; // Admin can bypass model_id checks
        }else{

            model_id = message.getSubThread().getThread().getModelId();
        }

        // Ensure user exists before creating message
        // Fetch model_id from the grandparent thread to enforce the NOT NULL constraint

        userService.ensureUserExists(username, model_id);

        Message savedMessage = messageRepo.save(message);
        userService.addMessageToUser(savedMessage.getUserId(), savedMessage.getBody());

        return savedMessage;
    }

    public void deleteMessage(Long id, String username) {
        Message messageToDelete = messageRepo.findById(id).orElse(null);
        if (messageToDelete != null) {
            // Check if the requesting user is an admin or the owner of the message
            // if (requestingUser.getRole() == Role.ADMIN || ... )
            User requestingUser = userService.getUserByUsername(username);

            if (requestingUser.getRole().toString().equals("ADMIN") || requestingUser.getId().toString().equals(messageToDelete.getUserId().toString())) {
                messageRepo.delete(messageToDelete);
            } else {
                // Handle the case where the user is not authorized to delete the message
                throw new SecurityException("You do not have permission to delete this message.");
            }
        }
    }

    /*public Message updateMessage(Long id, Message updatedMessage) {
        return messageRepo.findById(id).map(message -> {
            message.setBody(updatedMessage.getBody());
            message.setUpdatedAt(java.time.Instant.now());
            return messageRepo.save(message);
        }).orElse(null);
    }*/

    public Message updateMessage(Long id, Message updatedMessage, String username) {
        
         // Check if the requesting user is an admin or the owner of the message
         // if (requestingUser.getRole() == Role.ADMIN || ... )
         User requestingUser = userService.getUserByUsername(username);

        if (requestingUser.getRole().toString().equals("ADMIN") || requestingUser.getId().toString().equals(messageRepo.findById(id).orElse(null).getUserId().toString())) {

        return messageRepo.findById(id).map(message -> {
            message.setBody(updatedMessage.getBody());
            message.setUpdatedAt(java.time.Instant.now());
            Message savedMessage = messageRepo.save(message);
            //userService.addMessageToUser(savedMessage.getUserId(), savedMessage.getBody());
            return savedMessage;
        }).orElse(null);
    }
    else{
        throw new SecurityException("You do not have permission to update this message.");
    }

  }

}
