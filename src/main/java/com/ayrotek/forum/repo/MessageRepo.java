package com.ayrotek.forum.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayrotek.forum.entity.Message;

public interface MessageRepo extends JpaRepository<Message, Long> {

    List<Message> findBySubThreadIdOrderByUpvoteCountDesc(Long subThreadId);

    String findUsernameByUserId(String userId);

}

