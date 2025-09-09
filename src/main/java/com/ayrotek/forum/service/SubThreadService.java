package com.ayrotek.forum.service;
import com.ayrotek.forum.entity.SubThread;
import com.ayrotek.forum.exception.MissingRelationException;
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

        if(subThreadRepo.count() < threadId){
            throw new IllegalArgumentException("No such thread with ID: " + threadId);
        }

        return subThreadRepo.findByThreadId(threadId);
    }

    public SubThread getSubThreadById(Long id) {
        return subThreadRepo.findById(id).orElse(null);
    }

    public void deleteSubThread(Long id) {
        subThreadRepo.deleteById(id);
    }

    public SubThread createSubThread(SubThread subThread) {
        // Validate that subthread has a parent thread
        if (subThread.getThread() == null) {
            throw new MissingRelationException("SubThread must have a parent Thread");
        }
        
        // Get the thread's model_id - this ensures consistency across the forum
        String threadModelId = subThread.getThread().getModelId();
        
        // Ensure user exists and validate that user's model_id matches thread's model_id
        // This prevents users from different models from posting in threads from other models
        String Temp_id = subThread.getUserId();
        String username = subThreadRepo.findUsernameByUserId(Temp_id);
        
        userService.ensureUserExists(username, threadModelId);

        subThread.setTitle(subThread.getTitle());
        //subThread.setContent(subThread.getContent());
        subThread.setUserId(subThread.getUserId());
        return subThreadRepo.save(subThread);
    }
}
