package com.ayrotek.forum.service;
import com.ayrotek.forum.entity.SubThread;
import com.ayrotek.forum.repo.SubThreadRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.Instant;


@Service
public class SubThreadService {

    private final SubThreadRepo subThreadRepo;

    @Autowired
    public SubThreadService(SubThreadRepo subThreadRepo) {
        this.subThreadRepo = subThreadRepo;
    }

    public List<SubThread> getAllSubThreadsByThreadId(Long threadId) {
        return subThreadRepo.findByThreadId(threadId);
    }

    public SubThread getSubThreadById(Long id) {
        return subThreadRepo.findById(id).orElse(null);
    }

    public void deleteSubThread(Long id) {
        subThreadRepo.deleteById(id);
    }

    public SubThread createSubThread(SubThread subThread) {
        subThread.setTitle(subThread.getTitle());
        subThread.setCreatedAt(Instant.now());
        subThread.setContent(subThread.getContent());
        subThread.setUserId(subThread.getUserId());
        

        return subThreadRepo.save(subThread);
    }
}
