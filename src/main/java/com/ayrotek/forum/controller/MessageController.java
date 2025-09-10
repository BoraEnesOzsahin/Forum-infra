package com.ayrotek.forum.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ayrotek.forum.entity.SubThread;
import com.ayrotek.forum.service.MessageService;
import com.ayrotek.forum.entity.Message;
import com.ayrotek.forum.entity.ServerResponse;
import com.ayrotek.forum.dto.MessageDto;
import com.ayrotek.forum.dto.DtoMapper;
import com.ayrotek.forum.service.SubThreadService;
import com.ayrotek.forum.dto.DeleteMessageRequestDto;


import com.ayrotek.forum.entity.ServerResponse;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final SubThreadService subThreadService; // or SubThreadRepo

    public MessageController(MessageService messageService, SubThreadService subThreadService) {
        this.messageService = messageService;
        this.subThreadService = subThreadService;
    }

    @PostMapping("/createMessage")
    public ServerResponse createMessage(@RequestBody MessageDto messageDto) {
        SubThread subThread = subThreadService.getSubThreadById(messageDto.getSubThreadId());
        Message message = DtoMapper.toEntity(messageDto, subThread);
        Message saved = messageService.createMessage(message, messageDto.getUsername());
        return new ServerResponse(true, "Message created successfully", DtoMapper.toDto(saved));
    }

    

    @DeleteMapping("/deleteMessage/{id}")
    public ServerResponse deleteMessage(@PathVariable Long id, @RequestBody DeleteMessageRequestDto deleteRequest) {
        messageService.deleteMessage(id, deleteRequest.getUsername());
        return new ServerResponse(true, "Message deleted successfully", null);
    }



    @GetMapping("/allMessagesBySubThreadId/{subThreadId}")
    public ServerResponse getAllMessages(@PathVariable Long subThreadId) {
        List<Message> messages = messageService.getAllMessagesBySubThreadId(subThreadId);
        return new ServerResponse(true, "Messages fetched successfully", DtoMapper.toMessageDtoList(messages));
    }

    /*@PutMapping("/updateMessage/{id}")
    public ServerResponse updateMessage(@PathVariable Long id, @RequestBody MessageDto messageDto) {
        SubThread subThread = subThreadService.getSubThreadById(messageDto.getSubThreadId());
        Message updatedEntity = DtoMapper.toEntity(messageDto, subThread);
        Message updated = messageService.updateMessage(id, updatedEntity);
        return new ServerResponse(true, "Message updated successfully", DtoMapper.toDto(updated));
    }*/

}
