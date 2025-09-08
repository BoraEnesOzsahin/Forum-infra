package com.ayrotek.forum.service;

import com.ayrotek.forum.entity.Thread;
import com.ayrotek.forum.repo.ThreadRepo;
import java.util.List;
import org.springframework.stereotype.Service;

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
        // Simply save the thread - Lombok @Data provides all getters/setters
        // and @PrePersist will set createdAt automatically
        userService.ensureUserExists(thread.getUserId(), thread.getModelId());
        return threadRepo.save(thread);
    }

    
}
