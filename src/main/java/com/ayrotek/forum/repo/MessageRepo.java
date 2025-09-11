package com.ayrotek.forum.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ayrotek.forum.entity.Message;

public interface MessageRepo extends JpaRepository<Message, Long> {

    @Query("""
           select m from Message m
           join fetch m.subThread st
           join fetch st.thread t
           where m.id = :id
           """)
    Optional<Message> findByIdWithSubThreadAndThread(@Param("id") Long id);

    @Query("""
           select m from Message m
           join fetch m.subThread st
           join fetch st.thread t
           where st.id = :subThreadId
           order by m.upvoteCount desc
           """)
    List<Message> findBySubThreadIdWithJoinFetch(@Param("subThreadId") Long subThreadId);

    String findUsernameByUserId(String userId);

    Message findMessageById(Long id);

    List<Message> findBySubThreadIdOrderByUpvoteCountDesc(Long subThreadId);

}

