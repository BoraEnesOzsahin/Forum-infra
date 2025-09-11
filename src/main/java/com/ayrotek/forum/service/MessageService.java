package com.ayrotek.forum.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayrotek.forum.repo.MessageRepo;
import com.ayrotek.forum.entity.Message;
import com.ayrotek.forum.entity.SubThread;
import com.ayrotek.forum.entity.Thread;
import com.ayrotek.forum.entity.User;
import com.ayrotek.forum.repo.SubThreadRepo;

@Service
public class MessageService {

    private final MessageRepo messageRepo;
    private final UserService userService;
    private final SubThreadRepo subThreadRepo;

    public MessageService(MessageRepo messageRepo,
                          UserService userService,
                          SubThreadRepo subThreadRepo) {
        this.messageRepo = messageRepo;
        this.userService = userService;
        this.subThreadRepo = subThreadRepo;
    }

    @Transactional(readOnly = true)
    public Message getMessageById(Long id) {
        return messageRepo.findByIdWithSubThreadAndThread(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Message> getAllMessagesBySubThreadId(Long subThreadId) {
        return messageRepo.findBySubThreadIdWithJoinFetch(subThreadId);
    }

    public Message createMessage(Message message, String username) {
        if (message.getSubThread() == null || message.getSubThread().getId() == null) {
            throw new IllegalArgumentException("subThreadId required");
        }
        SubThread subThread = subThreadRepo.findById(message.getSubThread().getId())
                .orElseThrow(() -> new IllegalArgumentException("SubThread not found: " + message.getSubThread().getId()));

        Thread parentThread = subThread.getThread();
        if (parentThread == null) {
            throw new IllegalStateException("Parent thread missing for subthread " + subThread.getId());
        }

        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new IllegalStateException("User not found: " + username);
        }

        // Admin bypass, otherwise enforce modelId match
        if (user.getRole() != User.Role.ADMIN) {
            if (user.getModelId() == null || !user.getModelId().equals(parentThread.getModelId())) {
                throw new IllegalStateException("Model mismatch: user cannot post to this thread");
            }
        }

        // Set owner
        message.setUserId(String.valueOf(user.getId()));
        message.setSubThread(subThread);

        Message saved = messageRepo.save(message);
        // (Optional) track message content for user
        userService.addMessageToUser(saved.getUserId(), saved.getBody());
        return saved;
    }

    @Transactional
    public Message updateMessage(Long id, String newBody, String username) {
        Message existing = messageRepo.findByIdWithSubThreadAndThread(id)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + id));

        User user = userService.getUserByUsername(username);
        if (user == null) throw new IllegalStateException("User not found: " + username);

        boolean admin = user.getRole() == User.Role.ADMIN;

        if (!admin && !String.valueOf(user.getId()).equals(existing.getUserId())) {
            throw new SecurityException("Not authorized to update this message");
        }

        if (!admin) {
            // parent thread is already fetched
            if (user.getModelId() == null ||
                !user.getModelId().equals(existing.getSubThread().getThread().getModelId())) {
                throw new SecurityException("Model mismatch on update");
            }
        }

        existing.setBody(newBody);
        existing.setUpdatedAt(java.time.Instant.now());
        return messageRepo.save(existing);
    }

    public void deleteMessage(Long id, String username) {
        Message existing = messageRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + id));

        User user = userService.getUserByUsername(username);
        if (user == null) throw new IllegalStateException("User not found: " + username);

        boolean admin = user.getRole() == User.Role.ADMIN;
        if (!admin && !String.valueOf(user.getId()).equals(existing.getUserId())) {
            throw new SecurityException("Not authorized to delete this message");
        }
        messageRepo.delete(existing);
    }
}
