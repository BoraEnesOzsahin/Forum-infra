package com.ayrotek.forum.controller;
import com.ayrotek.forum.service.ThreadService;
import com.ayrotek.forum.dto.ThreadDto;
import com.ayrotek.forum.dto.DtoMapper;
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
import com.ayrotek.forum.entity.ServerResponse;


@RestController
@RequestMapping("/threads")
public class ThreadController {

    private final ThreadService threadService;

    @Autowired
    public ThreadController(ThreadService threadService) {
        this.threadService = threadService;
    }

    @GetMapping("/all")
    public ServerResponse getAllThreads() {
        List<Thread> threads = threadService.getAllThreads();
        return new ServerResponse(true, "Threads fetched successfully", DtoMapper.toThreadDtoList(threads));
    }

    @GetMapping("/{id}")
    public ServerResponse getThreadById(@PathVariable Long id) {
        Thread thread = threadService.getThreadById(id);
        if (thread == null) {
            return new ServerResponse(false, "Thread not found", null);
        }
        return new ServerResponse(true, "Thread fetched successfully", DtoMapper.toDto(thread));
    }

    @PostMapping("/createThread")
    public ServerResponse createThread(@RequestBody ThreadDto threadDto) {
        Thread thread = DtoMapper.toEntity(threadDto);
        Thread saved = threadService.createThread(thread);
        return new ServerResponse(true, "Thread created successfully", DtoMapper.toDto(saved));
    }

    @DeleteMapping("/deleteThread/{id}")
    public ServerResponse deleteThread(@PathVariable Long id) {
        threadService.deleteThread(id);
        return new ServerResponse(true, "Thread deleted successfully", null);
    }
}
