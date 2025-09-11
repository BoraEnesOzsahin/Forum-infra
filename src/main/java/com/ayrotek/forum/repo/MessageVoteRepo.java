package com.ayrotek.forum.repo;

import com.ayrotek.forum.entity.MessageVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageVoteRepo extends JpaRepository<MessageVote, MessageVote.PK> {

    Optional<MessageVote> findByIdMessageIdAndIdUserId(Long messageId, Long userId);

    List<MessageVote> findByIdMessageId(Long messageId);
}

