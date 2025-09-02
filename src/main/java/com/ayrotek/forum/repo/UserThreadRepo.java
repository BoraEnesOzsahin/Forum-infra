package com.ayrotek.forum.repo;

import com.ayrotek.forum.entity.Thread;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserThreadRepo extends JpaRepository<Thread, Long> {
    
}
