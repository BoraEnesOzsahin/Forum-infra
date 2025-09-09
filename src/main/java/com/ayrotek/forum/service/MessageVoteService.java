package com.ayrotek.forum.service;

import com.ayrotek.forum.entity.Message;
import com.ayrotek.forum.entity.MessageVote;
import com.ayrotek.forum.entity.User;
import com.ayrotek.forum.exception.IdMismatchException;
import com.ayrotek.forum.exception.UserNotFoundException;
import com.ayrotek.forum.repo.MessageRepo;
import com.ayrotek.forum.repo.MessageVoteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import com.ayrotek.forum.dto.DtoMapper;
import com.ayrotek.forum.dto.MessageVoteDto;

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
        messageVote.setUserId(user.getId());

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
    public void deleteVote(Long userId, Long messageId) { // **FIX**: Changed parameter from String to Long
        messageVoteRepo.deleteByUserIdAndMessageId(userId, messageId);

        // **IMPROVEMENT**: Decrement the upvote count efficiently.
        Message message = messageRepo.findById(messageId).orElse(null);
        if (message != null && message.getUpvoteCount() > 0) {
            message.setUpvoteCount(message.getUpvoteCount() - 1);
            messageRepo.save(message);
        }
    }

    @Transactional
    public MessageVote createVote(MessageVoteDto voteDto) {
        // 1. Get the User object from the database using the username from the DTO
        User user = userService.getUserByUsername(voteDto.getUsername());
        if (user == null) {
            throw new UserNotFoundException("User not found with username: " + voteDto.getUsername());
        }

        // 2. Use the simple, clean mapper to create the entity
        MessageVote vote = DtoMapper.toEntity(voteDto);

        // 3. **THIS IS THE LOGIC MOVED FROM THE MAPPER**: Manually set the Long userId
        vote.setUserId(user.getId());

        // 4. Perform any other business logic (like checking for existing votes)
        // Optional<MessageVote> existingVote = messageVoteRepo.findByUserIdAndMessageId(...);
        // if (existingVote.isPresent()) { ... }

        // 5. Save the now-complete entity
        return messageVoteRepo.save(vote);
    }
}
