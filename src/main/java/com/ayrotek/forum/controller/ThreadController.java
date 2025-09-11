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
import java.util.stream.Collectors;
import com.ayrotek.forum.entity.Thread;
import com.ayrotek.forum.entity.ServerResponse;
import com.ayrotek.forum.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


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

        List<ThreadDto> threadDtos = threads.stream()
                .map(thread -> {
                    ThreadDto dto = DtoMapper.toDto(thread);
                    populateUsername(thread, dto);
                    return dto;
                })
                .collect(Collectors.toList());

        return new ServerResponse(true, "Threads fetched successfully", threadDtos);
    }

    @GetMapping("/{id}")
    public ServerResponse getThreadById(@PathVariable Long id) {
        Thread thread = threadService.getThreadById(id);
        if (thread == null) {
            return new ServerResponse(false, "Thread not found", null);
        }
        ThreadDto dto = DtoMapper.toDto(thread);
        populateUsername(thread, dto);
        return new ServerResponse(true, "Thread fetched successfully", dto);
    }

    @PostMapping("/createThread")
    public ResponseEntity<ServerResponse> createThread(@RequestBody ThreadDto threadDto) {
        try {
            String actingUsername = firstNonNull(threadDto.getUsername(), currentUsername());
            if (actingUsername == null) {
                return ResponseEntity.status(401)
                        .body(new ServerResponse(false, "No user context provided", null));
            }
            Thread thread = DtoMapper.toEntity(threadDto);
            Thread saved = threadService.createThread(thread, actingUsername);
            ThreadDto responseDto = DtoMapper.toDto(saved);
            populateUsername(saved, responseDto);
            return ResponseEntity.ok(new ServerResponse(true, "Thread created successfully", responseDto));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ServerResponse(false, "Error creating thread: " + e.getMessage(), null));
        }
    }

    @PostMapping("/updateThread/{id}")
    public ResponseEntity<ServerResponse> updateThread(@PathVariable Long id,
                                                       @RequestBody ThreadDto threadDto) {
        try {
            Thread existing = threadService.getThreadById(id);
            if (existing == null) {
                return ResponseEntity.status(404).body(new ServerResponse(false, "Thread not found", null));
            }
            String actingUsername = firstNonNull(threadDto.getUsername(), currentUsername());
            if (actingUsername == null) {
                return ResponseEntity.status(401)
                        .body(new ServerResponse(false, "No user context provided", null));
            }

            Thread incoming = DtoMapper.toEntity(threadDto); // includes potential modelId (admin only)
            Thread saved = threadService.updateExistingThread(existing, incoming, actingUsername);

            ThreadDto responseDto = DtoMapper.toDto(saved);
            populateUsername(saved, responseDto);
            return ResponseEntity.ok(new ServerResponse(true, "Thread updated successfully", responseDto));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ServerResponse(false, "Error updating thread: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/deleteThread/{id}")
    public ResponseEntity<ServerResponse> deleteThread(@PathVariable Long id,
                                                       @RequestBody(required = false) ThreadDto threadDto) {
        try {
            String actingUsername = firstNonNull(
                    threadDto != null ? threadDto.getUsername() : null,
                    currentUsername());
            if (actingUsername == null) {
                return ResponseEntity.status(401)
                        .body(new ServerResponse(false, "No user context provided", null));
            }

            threadService.assertUserCanModifyThread(id, actingUsername);

            threadService.deleteThread(id);
            return ResponseEntity.ok(new ServerResponse(true, "Thread deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ServerResponse(false, "Error deleting thread: " + e.getMessage(), null));
        }
    }

    private void populateUsername(Thread thread, ThreadDto dto) {
        // TODO: optimize (bulk user lookup) to avoid N+1 queries in /all
        String rawUserId = thread.getUserId();
        if (rawUserId == null) return;
        User user = null;
        try {
            user = userService.getUserById(Long.parseLong(rawUserId));
        } catch (NumberFormatException ex) {
            user = userService.getUserByUsername(rawUserId);
        }
        if (user != null) {
            dto.setUsername(user.getUsername());
        }
    }

    private String currentUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) return null;
            Object principal = auth.getPrincipal();
            if (principal instanceof UserDetails ud) return ud.getUsername();
            return principal.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private <T> T firstNonNull(T a, T b) {
        return a != null ? a : b;
    }
}
