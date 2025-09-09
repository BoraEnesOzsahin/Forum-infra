package com.ayrotek.forum.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ayrotek.forum.service.MessageVoteService;
import com.ayrotek.forum.dto.MessageVoteDto;
import com.ayrotek.forum.dto.DtoMapper;
import com.ayrotek.forum.entity.MessageVote;
import com.ayrotek.forum.entity.ServerResponse;

@RestController
@RequestMapping("/messageVotes")
public class MessageVoteController {

    private final MessageVoteService messageVoteService;

    public MessageVoteController(MessageVoteService messageVoteService) {
        this.messageVoteService = messageVoteService;
    }


    @PostMapping("/createMessageVote")
    public ServerResponse createMessageVote(@RequestBody MessageVoteDto messageVoteDto) {
        MessageVote messageVote = DtoMapper.toEntity(messageVoteDto);
        messageVoteService.saveVote(messageVote, messageVoteDto.getUsername() );
        return new ServerResponse(true, "Message vote saved successfully", DtoMapper.toDto(messageVote));
    }
}
