package com.ayrotek.forum.controller;
import com.ayrotek.forum.service.ThreadService;
import com.ayrotek.forum.service.UserService;
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
import org.springframework.http.ResponseEntity;
import java.util.List;
import com.ayrotek.forum.entity.Thread;
import com.ayrotek.forum.entity.ServerResponse;
import com.ayrotek.forum.entity.User;


@RestController
@RequestMapping("/threads")
public class ThreadController {

    private final ThreadService threadService;
    private final UserService userService;

    @Autowired
    public ThreadController(ThreadService threadService, UserService userService) {
        this.threadService = threadService;
        this.userService = userService;
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
    public ResponseEntity<ServerResponse> createThread(@RequestBody ThreadDto threadDto) {
        try {
            // Get the user by username to find their ID
            User user = userService.getUserByUsername(threadDto.getUsername());
            if (user == null) {
                return ResponseEntity.status(404).body(new ServerResponse(false, "User not found", null));
            }

            // Convert DTO to entity and set the user ID
            Thread thread = DtoMapper.toEntity(threadDto);
            thread.setUserId(String.valueOf(user.getId()));

            // Call the service with the entity AND the username for validation
            Thread saved = threadService.createThread(thread, threadDto.getUsername());
            
            return ResponseEntity.ok(new ServerResponse(true, "Thread created successfully", DtoMapper.toDto(saved)));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerResponse(false, "Error creating thread: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/deleteThread/{id}")
    public ServerResponse deleteThread(@PathVariable Long id) {
        threadService.deleteThread(id);
        return new ServerResponse(true, "Thread deleted successfully", null);
    }
}
