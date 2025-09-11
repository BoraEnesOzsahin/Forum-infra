package com.ayrotek.forum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ayrotek.forum.service.SubThreadService;
import com.ayrotek.forum.service.ThreadService;
import com.ayrotek.forum.service.UserService;
import com.ayrotek.forum.entity.SubThread;
import com.ayrotek.forum.dto.SubThreadDto;
import com.ayrotek.forum.dto.DtoMapper;
import com.ayrotek.forum.entity.ServerResponse;
import com.ayrotek.forum.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/subThreads")
public class SubThreadController {

    private final SubThreadService subThreadService;
    private final ThreadService threadService;
    private final UserService userService;

    @Autowired
    public SubThreadController(SubThreadService subThreadService,
                               ThreadService threadService,
                               UserService userService) {
        this.subThreadService = subThreadService;
        this.threadService = threadService;
        this.userService = userService;
    }

    @GetMapping("/allSubThreadsByThreadId/{threadId}")
    public ResponseEntity<ServerResponse> getAllSubThreads(@PathVariable Long threadId) {
        try {
            List<SubThread> subThreads = subThreadService.getAllSubThreadsByThreadId(threadId);
            List<SubThreadDto> dtoList = subThreads.stream().map(st -> {
                SubThreadDto dto = DtoMapper.toDto(st);
                populateUsername(st, dto);
                return dto;
            }).toList();
            return ResponseEntity.ok(new ServerResponse(true, "SubThreads fetched successfully", dtoList));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ServerResponse(false, "Error fetching subthreads: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServerResponse> getSubThreadById(@PathVariable Long id) {
        try {
            SubThread subThread = subThreadService.getSubThreadById(id);
            if (subThread == null) {
                return ResponseEntity.status(404)
                        .body(new ServerResponse(false, "SubThread not found", null));
            }
            SubThreadDto dto = DtoMapper.toDto(subThread);
            populateUsername(subThread, dto);
            return ResponseEntity.ok(new ServerResponse(true, "SubThread fetched successfully", dto));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ServerResponse(false, "Error fetching subthread: " + e.getMessage(), null));
        }
    }

    @PostMapping("/createSubThread")
    public ResponseEntity<ServerResponse> createSubThread(@RequestBody SubThreadDto subThreadDto) {
        try {
            if (subThreadDto.getThreadId() == null) {
                return ResponseEntity.status(400)
                        .body(new ServerResponse(false, "threadId required", null));
            }
            var parent = threadService.getThreadById(subThreadDto.getThreadId());
            if (parent == null) {
                return ResponseEntity.status(404)
                        .body(new ServerResponse(false, "Parent thread not found", null));
            }
            String actingUsername = firstNonNull(subThreadDto.getUsername(), currentUsername());
            if (actingUsername == null) {
                return ResponseEntity.status(401)
                        .body(new ServerResponse(false, "No user context provided", null));
            }
            SubThread subThread = DtoMapper.toEntity(subThreadDto, parent);
            SubThread saved = subThreadService.createSubThread(subThread, actingUsername);
            SubThreadDto dto = DtoMapper.toDto(saved);
            populateUsername(saved, dto);
            return ResponseEntity.ok(new ServerResponse(true, "SubThread created successfully", dto));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ServerResponse(false, "Error creating subthread: " + e.getMessage(), null));
        }
    }

    @PostMapping("/updateSubThread/{id}")
    public ResponseEntity<ServerResponse> updateSubThread(@PathVariable Long id,
                                                          @RequestBody SubThreadDto subThreadDto) {
        try {
            String actingUsername = firstNonNull(subThreadDto.getUsername(), currentUsername());
            if (actingUsername == null) {
                return ResponseEntity.status(401)
                        .body(new ServerResponse(false, "No user context provided", null));
            }
            SubThread patch = new SubThread();
            if (subThreadDto.getTitle() != null) patch.setTitle(subThreadDto.getTitle());

            SubThread saved = subThreadService.updateSubThread(id, patch, actingUsername);
            SubThreadDto dto = DtoMapper.toDto(saved);
            populateUsername(saved, dto);
            return ResponseEntity.ok(new ServerResponse(true, "SubThread updated successfully", dto));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ServerResponse(false, "Error updating subthread: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/deleteSubThread/{id}")
    public ResponseEntity<ServerResponse> deleteSubThread(@PathVariable Long id,
                                                          @RequestBody(required = false) SubThreadDto subThreadDto) {
        try {
            String actingUsername = firstNonNull(
                    subThreadDto != null ? subThreadDto.getUsername() : null,
                    currentUsername());
            if (actingUsername == null) {
                return ResponseEntity.status(401)
                        .body(new ServerResponse(false, "No user context provided", null));
            }
            subThreadService.deleteSubThread(id, actingUsername);
            return ResponseEntity.ok(new ServerResponse(true, "SubThread deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ServerResponse(false, "Error deleting subthread: " + e.getMessage(), null));
        }
    }

    // Helpers
    private void populateUsername(SubThread subThread, SubThreadDto dto) {
        try {
            String rawUserId = subThread.getUserId();
            if (rawUserId == null) return;
            if (!isNumeric(rawUserId)) return;
            User user = userService.getUserById(Long.parseLong(rawUserId));
            if (user != null) dto.setUsername(user.getUsername());
        } catch (Exception ignored) { }
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

    private <T> T firstNonNull(T a, T b) { return a != null ? a : b; }

    private boolean isNumeric(String s) {
        if (s == null || s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) if (!Character.isDigit(s.charAt(i))) return false;
        return true;
    }
}
