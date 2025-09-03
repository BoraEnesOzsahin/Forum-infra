package com.ayrotek.forum.service;

import com.ayrotek.forum.entity.Thread;
import com.ayrotek.forum.repo.ThreadRepo;
import com.ayrotek.forum.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;




@Service
public class ThreadService {

    private final ThreadRepo threadRepo;
    private final UserService userService;

    public ThreadService(ThreadRepo threadRepo, UserService userService) {
        this.threadRepo = threadRepo;
        this.userService = userService;
    }

    public List<Thread> getAllThreads() {
        return threadRepo.findAll();
    }

    public Thread getThreadById(Long id) {
        return threadRepo.findById(id).orElse(null);
    }


    public void deleteThread(Long id) {
        threadRepo.deleteById(id);
    }



     public Thread createThread(Thread thread) {
        // Ensure user exists before creating thread
    userService.ensureUserExists(String.valueOf(thread.getUserId()), thread.getModelId());
        // ...existing code...
        thread.setUserId(thread.getUserId());
        thread.setTitle(thread.getTitle());
        thread.setModelId(thread.getModelId());
        thread.setRole(thread.getRole());
        // createdAt is set by @PrePersist
        return threadRepo.save(thread);
    }

    
}
 