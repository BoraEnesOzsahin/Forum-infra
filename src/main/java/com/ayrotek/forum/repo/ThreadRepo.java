
package com.ayrotek.forum.repo;

import com.ayrotek.forum.entity.Thread;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadRepo extends JpaRepository<Thread, Long> {
	// JpaRepository provides all basic CRUD methods
}
