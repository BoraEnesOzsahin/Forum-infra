package com.ayrotek.forum.repo;

import com.ayrotek.forum.entity.Thread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ThreadRepo extends JpaRepository<Thread, Long> {
	// JpaRepository provides all basic CRUD methods

	String findUsernameByUserId(String userId);

	Page<Thread> findByUserId(String userId, Pageable pageable);

    List<Thread> findByUserIdOrderByCreatedAtDesc(String userId);
}
