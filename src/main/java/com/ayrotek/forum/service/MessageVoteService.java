package com.ayrotek.forum.service;

import com.ayrotek.forum.entity.Message;
import com.ayrotek.forum.entity.MessageVote;
import com.ayrotek.forum.entity.User;
import com.ayrotek.forum.exception.IdMismatchException;
import com.ayrotek.forum.repo.MessageRepo;
import com.ayrotek.forum.repo.MessageVoteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MessageVoteService {

    private final MessageVoteRepo messageVoteRepo;
    private final MessageRepo messageRepo;
    private final UserService userService;

    @Autowired
    public MessageVoteService(MessageVoteRepo messageVoteRepo, MessageRepo messageRepo, UserService userService) {
        this.messageVoteRepo = messageVoteRepo;
        this.messageRepo = messageRepo;
        this.userService = userService;
    }

    public List<MessageVote> getVotesByMessageId(Long messageId) {
        return messageVoteRepo.findByMessageId(messageId);
    }

    @Transactional
    public MessageVote saveVote(MessageVote messageVote, String username) {
        // 1. Get the full User object from the database using the username.
        User user = userService.ensureUserExists(username, null);
        
        // 2. **FIX**: Set the userId as a String.
        messageVote.setUserId(user.getId().toString());

        // 3. Check if the user has already voted on this message.
        Optional<MessageVote> existingVote = messageVoteRepo.findByUserIdAndMessageId(messageVote.getUserId(), messageVote.getMessageId());
        if (existingVote.isPresent()) {
            throw new IdMismatchException("User '" + username + "' has already voted on this message.");
        }

        // 4. Save the new vote.
        messageVoteRepo.save(messageVote);

        // 5. **IMPROVEMENT**: Increment the upvote count efficiently.
        Message message = messageRepo.findById(messageVote.getMessageId()).orElse(null);
        if (message != null) {
            message.setUpvoteCount(message.getUpvoteCount() + 1);
            messageRepo.save(message);
        }
        
        return messageVote;
    }

    @Transactional
    public void deleteVote(String userId, Long messageId) { // **FIX**: Changed parameter from String to Long
        messageVoteRepo.deleteByUserIdAndMessageId(userId, messageId);

        // **IMPROVEMENT**: Decrement the upvote count efficiently.
        Message message = messageRepo.findById(messageId).orElse(null);
        if (message != null && message.getUpvoteCount() > 0) {
            message.setUpvoteCount(message.getUpvoteCount() - 1);
            messageRepo.save(message);
        }
    }
}
