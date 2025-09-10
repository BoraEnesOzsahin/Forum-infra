package com.ayrotek.forum.service;

import com.ayrotek.forum.entity.Thread;
import com.ayrotek.forum.repo.ThreadRepo;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThreadService {

    private final ThreadRepo threadRepo;
    private final UserService userService;

    public ThreadService(ThreadRepo threadRepo, UserService userService) {
        this.threadRepo = threadRepo;
        this.userService = userService;
    }

    // **SIMPLE**: Just return the entities from the database
    public List<Thread> getAllThreads() {
        return threadRepo.findAll();
    }

    public Thread getThreadById(Long id) {
        return threadRepo.findById(id).orElse(null);
    }

    @Transactional
    public void deleteThread(Long id) {
        threadRepo.deleteById(id);
    }

    @Transactional
    public Thread createThread(Thread thread, String username) {
        userService.ensureUserExists(username, thread.getModelId());
        return threadRepo.save(thread);
    }
}

