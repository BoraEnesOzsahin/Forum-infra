package com.ayrotek.forum.repo;

import com.ayrotek.forum.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepo extends JpaRepository<Post, Long> {
	// JpaRepository provides all basic CRUD methods

}
