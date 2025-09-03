package com.ayrotek.forum.service;
import com.ayrotek.forum.entity.SubThread;
import com.ayrotek.forum.repo.SubThreadRepo;
import com.ayrotek.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.Instant;


@Service
public class SubThreadService {

    private final SubThreadRepo subThreadRepo;
    private final UserService userService;

    public SubThreadService(SubThreadRepo subThreadRepo, UserService userService) {
        this.subThreadRepo = subThreadRepo;
        this.userService = userService;
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
        // Ensure user exists before creating subthread
        // Fetch model_id from the parent thread to enforce the NOT NULL constraint
        String modelId = subThread.getThread().getModelId();
        userService.ensureUserExists(subThread.getUserId(), modelId);

        subThread.setTitle(subThread.getTitle());
        //subThread.setContent(subThread.getContent());
        subThread.setUserId(subThread.getUserId());
        return subThreadRepo.save(subThread);
    }
}
