package com.ayrotek.forum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ayrotek.forum.entity.SubThread;
import java.util.List;

public interface SubThreadRepo extends JpaRepository<SubThread, Long> {

    List<SubThread> findByThreadId(Long threadId);

    String findUsernameByUserId(String userId);
}