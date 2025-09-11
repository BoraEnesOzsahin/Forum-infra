package com.ayrotek.forum.service;

import com.ayrotek.forum.dto.MessageVoteDto;
import com.ayrotek.forum.entity.Message;
import com.ayrotek.forum.entity.MessageVote;
import com.ayrotek.forum.entity.SubThread;
import com.ayrotek.forum.entity.Thread;
import com.ayrotek.forum.entity.User;
import com.ayrotek.forum.exception.IdMismatchException;
import com.ayrotek.forum.exception.UserNotFoundException;
import com.ayrotek.forum.repo.MessageRepo;
import com.ayrotek.forum.repo.MessageVoteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import com.ayrotek.forum.dto.DtoMapper;

@Service
public class MessageVoteService {

    private final MessageVoteRepo messageVoteRepo;
    private final MessageRepo messageRepo;
    private final UserService userService;

    @Autowired
    public MessageVoteService(MessageVoteRepo messageVoteRepo,
                              MessageRepo messageRepo,
                              UserService userService) {
        this.messageVoteRepo = messageVoteRepo;
        this.messageRepo = messageRepo;
        this.userService = userService;
    }

    @Transactional
    public MessageVote createEnforcedVote(MessageVoteDto voteDto) {
        User user = fetchUser(voteDto.getUsername());
        Message message = fetchMessage(voteDto.getMessageId());
        enforceModelConstraint(user, message);

        var existing = messageVoteRepo.findByIdMessageIdAndIdUserId(message.getId(), user.getId());
        if (existing.isPresent()) {
            throw new IdMismatchException("User already voted on this message");
        }

        MessageVote vote = DtoMapper.toEntity(voteDto);
        vote.setMessageId(message.getId());
        vote.setUserId(user.getId());
        vote.setUpvoted(voteDto.isUpvoted());
        vote.setCreatedAt(Instant.now());

        messageVoteRepo.save(vote);
        if (vote.isUpvoted()) incrementMessageUpvotes(message, 1);
        return vote;
    }

    @Transactional
    public MessageVote toggleVote(MessageVoteDto voteDto) {
        User user = fetchUser(voteDto.getUsername());
        Message message = fetchMessage(voteDto.getMessageId());
        enforceModelConstraint(user, message);

        var existingOpt = messageVoteRepo.findByIdMessageIdAndIdUserId(message.getId(), user.getId());
        if (existingOpt.isEmpty()) {
            return createEnforcedVote(voteDto);
        }

        MessageVote existing = existingOpt.get();
        boolean oldVal = existing.isUpvoted();
        boolean newVal = voteDto.isUpvoted();

        if (oldVal == newVal) {
            messageVoteRepo.delete(existing);
            if (oldVal) incrementMessageUpvotes(message, -1);
            return existing;
        }

        existing.setUpvoted(newVal);
        existing.setUpdatedAt(Instant.now());
        messageVoteRepo.save(existing);

        if (oldVal && !newVal) incrementMessageUpvotes(message, -1);
        else if (!oldVal && newVal) incrementMessageUpvotes(message, 1);

        return existing;
    }

    @Transactional
    public void deleteVoteByUsername(String username, Long messageId) {
        User user = fetchUser(username);
        Message message = fetchMessage(messageId);
        var existing = messageVoteRepo.findByIdMessageIdAndIdUserId(messageId, user.getId());
        if (existing.isEmpty()) throw new IllegalArgumentException("Vote not found");
        MessageVote vote = existing.get();
        messageVoteRepo.delete(vote);
        if (vote.isUpvoted()) incrementMessageUpvotes(message, -1);
    }

    public List<MessageVote> getVotesByMessageId(Long messageId) {
        return messageVoteRepo.findByIdMessageId(messageId);
    }

    private User fetchUser(String username) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("username required");
        User user = userService.getUserByUsername(username);
        if (user == null) throw new UserNotFoundException("User not found: " + username);
        return user;
    }

    private Message fetchMessage(Long messageId) {
        if (messageId == null) throw new IllegalArgumentException("messageId required");
        return messageRepo.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));
    }

    private void enforceModelConstraint(User user, Message message) {
        if (user.getRole() == User.Role.ADMIN) return;
        SubThread st = message.getSubThread();
        if (st == null || st.getThread() == null) {
            throw new IllegalStateException("Message missing thread context");
        }
        Thread thread = st.getThread();
        if (user.getModelId() == null || !user.getModelId().equals(thread.getModelId())) {
            throw new SecurityException("Model mismatch: cannot vote on this message");
        }
    }

    private void incrementMessageUpvotes(Message message, int delta) {
        message.setUpvoteCount(message.getUpvoteCount() + delta);
        messageRepo.save(message);
    }
}
