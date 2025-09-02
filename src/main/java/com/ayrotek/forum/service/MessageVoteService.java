package com.ayrotek.forum.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.ayrotek.forum.repo.MessageVoteRepo;
import com.ayrotek.forum.entity.MessageVote;
import com.ayrotek.forum.entity.Message;

import java.util.List;
import com.ayrotek.forum.repo.MessageRepo;

@Service

public class MessageVoteService {

    private final MessageVoteRepo messageVoteRepo;
    private final MessageRepo messageRepo;

    @Autowired
    public MessageVoteService(MessageVoteRepo messageVoteRepo, MessageRepo messageRepo) {
        this.messageVoteRepo = messageVoteRepo;
        this.messageRepo = messageRepo;
    }


    public List<MessageVote> getVotesByMessageId(Long messageId) {
        return messageVoteRepo.findByMessageId(messageId);
    }

    public void saveVote(MessageVote messageVote) {
        messageVote.setCreatedAt(java.time.Instant.now());
        messageVoteRepo.save(messageVote);

        // Update upvoteCount in Message
        // (Assumes upvote = +1, downvote = -1, and only one vote per user per message)
        // You may want to improve this logic for toggling votes
        int upvoteCount = (int) messageVoteRepo.findByMessageId(messageVote.getMessageId())
            .stream().filter(MessageVote::isUpvoted).count();
        // Update the message entity
        // You need MessageRepo to do this
        // So inject MessageRepo
        // (Assuming field: private final MessageRepo messageRepo;)
        // (Assuming constructor injection)
        // (If not present, add MessageRepo injection)
        // Now update:
        com.ayrotek.forum.entity.Message message = messageRepo.findById(messageVote.getMessageId()).orElse(null);
        if (message != null) {
            message.setUpvoteCount(upvoteCount);
            messageRepo.save(message);
        }
    }

}
