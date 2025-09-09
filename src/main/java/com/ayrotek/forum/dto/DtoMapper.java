package com.ayrotek.forum.dto;

import com.ayrotek.forum.entity.*;
import java.util.List;
import java.util.stream.Collectors;

public class DtoMapper {

    // --- Thread ---
    public static ThreadDto toDto(Thread thread) {
        if (thread == null) return null;
        ThreadDto dto = new ThreadDto();
        dto.setId(thread.getId());
        // The username will be set by the service
        dto.setVehicleType(thread.getRole().toString());
        dto.setModelId(thread.getModelId());
        dto.setTitle(thread.getTitle());
        dto.setCreatedAt(thread.getCreatedAt());
        return dto;
    }

    public static Thread toEntity(ThreadDto dto) {
        if (dto == null) return null;
        Thread thread = new Thread();
        // The userId will be set by the service
        thread.setRole(Role.valueOf(dto.getVehicleType().toUpperCase()));
        thread.setModelId(dto.getModelId());
        thread.setTitle(dto.getTitle());
        return thread;
    }

    // --- SubThread ---
    public static SubThreadDto toDto(SubThread subThread) {
        if (subThread == null) return null;
        SubThreadDto dto = new SubThreadDto();
        dto.setId(subThread.getId());
        if (subThread.getThread() != null) {
            dto.setThreadId(subThread.getThread().getId());
        }
        // The username will be set by the service
        dto.setTitle(subThread.getTitle());
        dto.setCreatedAt(subThread.getCreatedAt());
        return dto;
    }
    
    public static SubThread toEntity(SubThreadDto dto, Thread thread) {
        if (dto == null) return null;
        SubThread subThread = new SubThread();
        // The userId will be set by the service
        subThread.setThread(thread);
        subThread.setTitle(dto.getTitle());
        return subThread;
    }

    // --- Message ---
    public static MessageDto toDto(Message message) {
        if (message == null) return null;
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setBody(message.getBody());
        if (message.getSubThread() != null) {
            dto.setSubThreadId(message.getSubThread().getId());
        }
        // The username will be set by the service
        return dto;
    }
    
    public static Message toEntity(MessageDto dto, SubThread subThread) {
        if (dto == null) return null;
        Message message = new Message();
        // The userId will be set by the service
        message.setBody(dto.getBody());
        message.setSubThread(subThread);
        return message;
    }

    // --- MessageVote ---
    public static MessageVoteDto toDto(MessageVote vote) {
        if (vote == null) return null;
        MessageVoteDto dto = new MessageVoteDto();
        dto.setMessageId(vote.getMessageId());
        // The username will be set by the service
        dto.setUpvoted(vote.isUpvoted());
        dto.setCreatedAt(vote.getCreatedAt());
        dto.setUpdatedAt(vote.getUpdatedAt());
        return dto;
    }
    
    public static MessageVote toEntity(MessageVoteDto dto) {
        if (dto == null) return null;
        MessageVote vote = new MessageVote();
        // The userId will be set by the service
        vote.setMessageId(dto.getMessageId());
        vote.setUpvoted(dto.isUpvoted());
        return vote;
    }

    // --- List Mappers ---
    public static List<ThreadDto> toThreadDtoList(List<Thread> threads) {
        return threads.stream().map(DtoMapper::toDto).collect(Collectors.toList());
    }
    public static List<SubThreadDto> toSubThreadDtoList(List<SubThread> subThreads) {
        return subThreads.stream().map(DtoMapper::toDto).collect(Collectors.toList());
    }
    public static List<MessageDto> toMessageDtoList(List<Message> messages) {
        return messages.stream().map(DtoMapper::toDto).collect(Collectors.toList());
    }
    public static List<MessageVoteDto> toMessageVoteDtoList(List<MessageVote> votes) {
        return votes.stream().map(DtoMapper::toDto).collect(Collectors.toList());
    }
}
