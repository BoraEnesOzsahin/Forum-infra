package com.ayrotek.forum.service;

import com.ayrotek.forum.entity.Thread;
import com.ayrotek.forum.repo.ThreadRepo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;



@Service
public class ThreadService {

    private final ThreadRepo threadRepo;

    @Autowired
    public ThreadService(ThreadRepo threadRepo) {
        this.threadRepo = threadRepo;
    }

    public List<Thread> getAllThreads() {
        return threadRepo.findAll();
    }

    public Thread getThreadById(Long id) {
        return threadRepo.findById(id).orElse(null);
    }

    public Thread createThread(Thread thread) {
        return threadRepo.save(thread);
    }

    public void deleteThread(Long id) {
        threadRepo.deleteById(id);
    }

}
