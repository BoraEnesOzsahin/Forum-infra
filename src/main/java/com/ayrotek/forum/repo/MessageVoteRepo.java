package com.ayrotek.forum.repo;
import com.ayrotek.forum.entity.MessageVote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional; // Import Optional


public interface MessageVoteRepo extends JpaRepository<MessageVote, MessageVote.PK> {

    List<MessageVote> findByMessageId(Long messageId);

    long countByMessageId(Long messageId);

    Optional<MessageVote> findByUserIdAndMessageId(Long userId, Long messageId);

    void deleteByUserIdAndMessageId(Long userId, Long messageId);
}

