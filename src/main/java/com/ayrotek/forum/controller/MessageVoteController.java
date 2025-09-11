package com.ayrotek.forum.controller;

import com.ayrotek.forum.dto.DtoMapper;
import com.ayrotek.forum.dto.MessageVoteDto;
import com.ayrotek.forum.entity.Message;
import com.ayrotek.forum.entity.MessageVote;
import com.ayrotek.forum.entity.ServerResponse;
import com.ayrotek.forum.entity.SubThread;
import com.ayrotek.forum.entity.Thread;
import com.ayrotek.forum.entity.User;
import com.ayrotek.forum.service.MessageVoteService;
import com.ayrotek.forum.service.MessageService;
import com.ayrotek.forum.service.SubThreadService;
import com.ayrotek.forum.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messageVotes")
public class MessageVoteController {

    private final MessageVoteService messageVoteService;
    private final MessageService messageService;
    private final SubThreadService subThreadService;
    private final UserService userService;

    public MessageVoteController(MessageVoteService messageVoteService,
                                 MessageService messageService,
                                 SubThreadService subThreadService,
                                 UserService userService) {
        this.messageVoteService = messageVoteService;
        this.messageService = messageService;
        this.subThreadService = subThreadService;
        this.userService = userService;
    }

    @PostMapping("/createMessageVote")
    public ResponseEntity<ServerResponse> createMessageVote(@RequestBody MessageVoteDto voteDto) {
        try {
            if (voteDto.getMessageId() == null) return bad("messageId required");
            String username = voteDto.getUsername();
            if (username == null || username.isBlank()) return bad("username required");

            MessageVote vote = messageVoteService.createEnforcedVote(voteDto);
            MessageVoteDto out = enrichDtoUser(DtoMapper.toDto(vote), vote.getUserId());
            return ok("Message vote saved successfully", out);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return bad(e.getMessage());
        } catch (SecurityException e) {
            return unauthorized(e.getMessage());
        } catch (Exception e) {
            return bad("Error: " + e.getMessage());
        }
    }

    @GetMapping("/viewMessageVotes/{messageId}")
    public ResponseEntity<ServerResponse> viewMessageVotes(@PathVariable Long messageId) {
        try {
            Message m = messageService.getMessageById(messageId);
            if (m == null) return notFound("Message not found");
            List<MessageVote> votes = messageVoteService.getVotesByMessageId(messageId);
            List<MessageVoteDto> dtoList = votes.stream()
                    .map(v -> enrichDtoUser(DtoMapper.toDto(v), v.getUserId()))
                    .toList();
            return ok("Votes fetched", dtoList);
        } catch (Exception e) {
            return bad("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteMessageVote")
    public ResponseEntity<ServerResponse> deleteMessageVote(@RequestParam Long messageId,
                                                            @RequestParam String username) {
        try {
            if (messageId == null) return bad("messageId required");
            if (username == null || username.isBlank()) return bad("username required");
            messageVoteService.deleteVoteByUsername(username, messageId);
            return ok("Vote deleted", null);
        } catch (IllegalArgumentException e) {
            return notFound(e.getMessage());
        } catch (SecurityException e) {
            return unauthorized(e.getMessage());
        } catch (Exception e) {
            return bad("Error: " + e.getMessage());
        }
    }

    // Optional toggle endpoint (kept separate)
    @PostMapping("/updateMessageVote")
    public ResponseEntity<ServerResponse> updateMessageVote(@RequestBody MessageVoteDto voteDto) {
        try {
            if (voteDto.getMessageId() == null) return bad("messageId required");
            if (voteDto.getUsername() == null || voteDto.getUsername().isBlank()) return bad("username required");
            MessageVote vote = messageVoteService.toggleVote(voteDto);
            MessageVoteDto out = enrichDtoUser(DtoMapper.toDto(vote), vote.getUserId());
            return ok("Vote toggled", out);
        } catch (IllegalArgumentException e) {
            return bad(e.getMessage());
        } catch (SecurityException e) {
            return unauthorized(e.getMessage());
        } catch (Exception e) {
            return bad("Error: " + e.getMessage());
        }
    }

    private MessageVoteDto enrichDtoUser(MessageVoteDto dto, Long userId) {
        if (userId != null) {
            User u = userService.getUserById(userId);
            if (u != null) {
                dto.setUserId(u.getId());
                dto.setUsername(u.getUsername());
            }
        }
        return dto;
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
}
