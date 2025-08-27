package com.ayrotek.forum.controller;
import com.ayrotek.forum.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import com.ayrotek.forum.entity.Thread;



@RestController
@RequestMapping("/threads")
public class ThreadController {

    private final ThreadService threadService;

    @Autowired
    public ThreadController(ThreadService threadService) {
        this.threadService = threadService;
    }

    @GetMapping
    public List<Thread> getAllThreads() {
        return threadService.getAllThreads();
    }

    @GetMapping("/{id}")
    public Thread getThreadById(@PathVariable Long id) {
        return threadService.getThreadById(id);
    }

    @PostMapping
    public Thread createThread(@RequestBody Thread thread) {
        return threadService.createThread(thread);
    }

    @DeleteMapping("/{id}")
    public void deleteThread(@PathVariable Long id) {
        threadService.deleteThread(id);
    }
}
