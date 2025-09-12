package com.ayrotek.forum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ayrotek.forum.entity.SubThread;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubThreadRepo extends JpaRepository<SubThread, Long> {

    List<SubThread> findByThreadId(Long threadId);

    String findUsernameByUserId(String userId);

    Page<SubThread> findByUserId(String userId, Pageable pageable);

    List<SubThread> findByUserIdOrderByCreatedAtDesc(String userId);
}