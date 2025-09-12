package com.ayrotek.forum.controller;

import com.ayrotek.forum.dto.DtoMapper;
import com.ayrotek.forum.dto.MessageDto;
import com.ayrotek.forum.dto.SubThreadDto;
import com.ayrotek.forum.dto.ThreadDto;
import com.ayrotek.forum.entity.Message;
import com.ayrotek.forum.entity.ServerResponse;
import com.ayrotek.forum.entity.SubThread;
import com.ayrotek.forum.entity.Thread;
import com.ayrotek.forum.service.UserContentService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserContentController {

    private final UserContentService userContentService;

    public UserContentController(UserContentService userContentService) {
        this.userContentService = userContentService;
    }

    // GET /users/{username}/threads?page=0&size=20
    @GetMapping("/{username}/threads")
    public ResponseEntity<ServerResponse> getUserThreads(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String acting = currentUsername();
        if (acting == null) return unauthorized("No user context");
        try {
            Page<Thread> pageData = userContentService.getThreadsForUser(acting, username, page, size);
            var content = pageData.getContent().stream()
                    .map(DtoMapper::toDto)
                    .map(ThreadNoUsernameDto::new) // <-- Remove username field
                    .toList();
            return ok("User threads fetched", paged(content, pageData));
        } catch (SecurityException se) {
            return unauthorized(se.getMessage());
        } catch (Exception e) {
            return bad("Error: " + e.getMessage());
        }
    }

    // GET /users/{username}/subthreads?page=0&size=20
    @GetMapping("/{username}/subthreads")
    public ResponseEntity<ServerResponse> getUserSubThreads(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String acting = currentUsername();
        if (acting == null) return unauthorized("No user context");
        try {
            Page<SubThread> pageData = userContentService.getSubThreadsForUser(acting, username, page, size);
            var content = pageData.getContent().stream()
                    .map(DtoMapper::toDto)
                    .peek(d -> d.setUsername(username))
                    .collect(Collectors.toList());
            return ok("User subthreads fetched", paged(content, pageData));
        } catch (SecurityException se) {
            return unauthorized(se.getMessage());
        } catch (Exception e) {
            return bad("Error: " + e.getMessage());
        }
    }

    // GET /users/{username}/messages?page=0&size=20
    @GetMapping("/{username}/messages")
    public ResponseEntity<ServerResponse> getUserMessages(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String acting = currentUsername();
        if (acting == null) return unauthorized("No user context");
        try {
            Page<Message> pageData = userContentService.getMessagesForUser(acting, username, page, size);
            var content = pageData.getContent().stream()
                    .map(m -> {
                        MessageDto dto = DtoMapper.toDto(m);
                        dto.setUsername(username);
                        return dto;
                    })
                    .collect(Collectors.toList());
            return ok("User messages fetched", paged(content, pageData));
        } catch (SecurityException se) {
            return unauthorized(se.getMessage());
        } catch (Exception e) {
            return bad("Error: " + e.getMessage());
        }
    }

    // ---- helpers ----

    private Map<String, Object> paged(Object content, Page<?> page) {
        Map<String, Object> m = new HashMap<>();
        m.put("content", content);
        m.put("page", page.getNumber());
        m.put("size", page.getSize());
        m.put("totalElements", page.getTotalElements());
        m.put("totalPages", page.getTotalPages());
        m.put("last", page.isLast());
        return m;
    }

    private String currentUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) return null;
            Object p = auth.getPrincipal();
            if (p instanceof UserDetails ud) return ud.getUsername();
            return p.toString();
        } catch (Exception e) { return null; }
    }

    private ResponseEntity<ServerResponse> ok(String msg, Object data) {
        return ResponseEntity.ok(new ServerResponse(true, msg, data));
    }
    private ResponseEntity<ServerResponse> bad(String msg) {
        return ResponseEntity.badRequest().body(new ServerResponse(false, msg, null));
    }
    private ResponseEntity<ServerResponse> unauthorized(String msg) {
        return ResponseEntity.status(401).body(new ServerResponse(false, msg, null));
    }

    // Add this DTO inside your controller
    public static class ThreadNoUsernameDto {
        public Long id;
        public String vehicleType;
        public String modelId;
        public String title;
        public java.time.Instant createdAt;
        public List<String> tags;
        public ThreadNoUsernameDto(ThreadDto dto) {
            this.id = dto.getId();
            this.vehicleType = dto.getVehicleType().toString();
            this.modelId = dto.getModelId();
            this.title = dto.getTitle();
            this.createdAt = dto.getCreatedAt();
            this.tags = dto.getTags();
        }
    }
}