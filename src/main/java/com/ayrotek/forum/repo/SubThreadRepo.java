package com.ayrotek.forum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ayrotek.forum.entity.SubThread;

public interface SubThreadRepo extends JpaRepository<SubThread, Long> {
    // additional custom methods if needed
}