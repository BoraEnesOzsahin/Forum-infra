package com.ayrotek.forum.repo;
import com.ayrotek.forum.entity.MessageVote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface MessageVoteRepo extends JpaRepository<MessageVote, MessageVote.PK> {

    List<MessageVote> findByMessageId(Long messageId);
}

