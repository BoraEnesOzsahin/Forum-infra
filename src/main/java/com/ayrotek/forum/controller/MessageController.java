package com.ayrotek.forum.controller;

import com.ayrotek.forum.dto.DtoMapper;
import com.ayrotek.forum.dto.MessageDto;
import com.ayrotek.forum.entity.Message;
import com.ayrotek.forum.entity.ServerResponse;
import com.ayrotek.forum.entity.SubThread;
import com.ayrotek.forum.entity.User;
import com.ayrotek.forum.service.MessageService;
import com.ayrotek.forum.service.SubThreadService;
import com.ayrotek.forum.service.UserService;
import com.ayrotek.forum.service.MessageVoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final SubThreadService subThreadService;
    private final UserService userService;
    private final MessageVoteService messageVoteService;

    public MessageController(MessageService messageService,
                             SubThreadService subThreadService,
                             UserService userService,
                             MessageVoteService messageVoteService) {
        this.messageService = messageService;
        this.subThreadService = subThreadService;
        this.userService = userService;
        this.messageVoteService = messageVoteService;
    }

    // View single
    @GetMapping("/viewMessage/{id}")
    public ResponseEntity<ServerResponse> viewMessage(@PathVariable Long id,
                                                      @RequestParam(name="includeVoters", defaultValue="false") boolean includeVoters) {
        Message m = messageService.getMessageById(id);
        if (m == null) return notFound("Message not found");
        MessageDto dto = DtoMapper.toDto(m);
        populateUsernameAndTimestamps(m, dto);
        if (includeVoters) dto.setVoters(fetchVoterUsernames(m.getId()));
        return ok("Message fetched", dto);
    }

    // View by subthread
    @GetMapping("/viewMessagesBySubThread/{subThreadId}")
    public ResponseEntity<ServerResponse> viewMessagesBySubThread(@PathVariable Long subThreadId,
                                                                  @RequestParam(name="includeVoters", defaultValue="false") boolean includeVoters) {
        SubThread st = subThreadService.getSubThreadById(subThreadId);
        if (st == null) return notFound("SubThread not found");
        List<Message> messages = messageService.getAllMessagesBySubThreadId(subThreadId); // already desc
        List<MessageDto> dtoList = messages.stream().map(m -> {
            MessageDto d = DtoMapper.toDto(m);
            populateUsernameAndTimestamps(m, d);
            if (includeVoters) d.setVoters(fetchVoterUsernames(m.getId()));
            return d;
        }).toList();
        return ok("Messages fetched", dtoList);
    }

    // Create
    @PostMapping("/createMessage")
    public ResponseEntity<ServerResponse> createMessage(@RequestBody MessageDto dto) {
        try {
            if (dto.getSubThreadId() == null) return bad("subThreadId required");
            if (dto.getBody() == null || dto.getBody().isBlank()) return bad("body required");
            String actingUsername = resolveActingUsername(dto.getUsername());
            if (actingUsername == null) return unauthorized("No user context");
            SubThread st = subThreadService.getSubThreadById(dto.getSubThreadId());
            if (st == null) return notFound("SubThread not found");
            Message entity = DtoMapper.toEntity(dto, st);
            Message saved = messageService.createMessage(entity, actingUsername);
            MessageDto out = DtoMapper.toDto(saved);
            populateUsernameAndTimestamps(saved, out);
            return ok("Message created", out);
        } catch (IllegalArgumentException e) {
            return bad(e.getMessage());
        } catch (SecurityException e) {
            return unauthorized(e.getMessage());
        } catch (Exception e) {
            return bad("Error: " + e.getMessage());
        }
    }

    // Update
    @PatchMapping("/updateMessage/{id}")
    public ResponseEntity<ServerResponse> updateMessage(@PathVariable Long id,
                                                        @RequestBody MessageDto dto) {
        try {
            if (dto.getBody() == null || dto.getBody().isBlank()) return bad("body required");
            String actingUsername = resolveActingUsername(dto.getUsername());
            if (actingUsername == null) return unauthorized("No user context");
            Message saved = messageService.updateMessage(id, dto.getBody(), actingUsername);
            MessageDto out = DtoMapper.toDto(saved);
            populateUsernameAndTimestamps(saved, out);
            return ok("Message updated", out);
        } catch (IllegalArgumentException e) {
            return notFound(e.getMessage());
        } catch (SecurityException e) {
            return unauthorized(e.getMessage());
        } catch (Exception e) {
            return bad("Error: " + e.getMessage());
        }
    }

    // Delete
    @DeleteMapping("/deleteMessage/{id}")
    public ResponseEntity<ServerResponse> deleteMessage(@PathVariable Long id,
                                                        @RequestParam(required = false) String username) {
        try {
            String actingUsername = resolveActingUsername(username);
            if (actingUsername == null) return unauthorized("No user context");
            messageService.deleteMessage(id, actingUsername);
            return ok("Message deleted", null);
        } catch (IllegalArgumentException e) {
            return notFound(e.getMessage());
        } catch (SecurityException e) {
            return unauthorized(e.getMessage());
        } catch (Exception e) {
            return bad("Error: " + e.getMessage());
        }
    }

    // Helpers
    private void populateUsernameAndTimestamps(Message m, MessageDto dto) {
        String raw = m.getUserId();
        if (raw != null && raw.chars().allMatch(Character::isDigit)) {
            try {
                User u = userService.getUserById(Long.parseLong(raw));
                if (u != null) {
                    dto.setUsername(u.getUsername());
                    dto.setUserId(u.getId());
                }
            } catch (Exception ignored) {}
        }
        dto.setCreatedAt(m.getCreatedAt());
        dto.setUpdatedAt(m.getUpdatedAt());
    }

    private String resolveActingUsername(String preferred) {
        if (preferred != null && !preferred.isBlank()) return preferred;
        return currentUsername();
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
    private ResponseEntity<ServerResponse> notFound(String msg) {
        return ResponseEntity.status(404).body(new ServerResponse(false, msg, null));
    }

    // helper to fetch voters
    private List<String> fetchVoterUsernames(Long messageId) {
        return messageVoteService.getVotesByMessageId(messageId).stream()
                .map(v -> userService.getUserById(v.getUserId()))
                .filter(u -> u != null)
                .map(User::getUsername)
                .toList();
    }
}
