package com.ayrotek.forum.dto;

import com.ayrotek.forum.entity.Message;
import com.ayrotek.forum.entity.MessageVote;
import com.ayrotek.forum.entity.SubThread;
import com.ayrotek.forum.entity.Thread;

import java.util.List;
import java.util.stream.Collectors;

public class DtoMapper {

    // Thread
    public static ThreadDto toDto(Thread thread) {
        if (thread == null) return null;
        ThreadDto dto = new ThreadDto();
        dto.setId(thread.getId());
        dto.setVehicleType(thread.getType());
        dto.setModelId(thread.getModelId());
        dto.setTitle(thread.getTitle());
        dto.setCreatedAt(thread.getCreatedAt());
        // Map tags from entity to DTO
        if (thread.getTags() != null) {
            dto.setTags(thread.getTags().stream().toList());
        }
        return dto;
    }

    public static Thread toEntity(ThreadDto dto) {
        if (dto == null) return null;
        Thread thread = new Thread();
        if (dto.getVehicleType() != null) {
            thread.setType(dto.getVehicleType());
        }
        thread.setModelId(dto.getModelId());
        thread.setTitle(dto.getTitle());
        return thread;
    }

    // SubThread
    public static SubThreadDto toDto(SubThread subThread) {
        if (subThread == null) return null;
        SubThreadDto dto = new SubThreadDto();
        dto.setId(subThread.getId());
        dto.setTitle(subThread.getTitle());
        if (subThread.getThread() != null) {
            dto.setThreadId(subThread.getThread().getId());
        }
        dto.setCreatedAt(subThread.getCreatedAt());
        if (subThread.getTags() != null) {
            dto.setTags(subThread.getTags().stream().toList());
        }
        return dto;
    }

    public static SubThread toEntity(SubThreadDto dto, Thread parentThread) {
        if (dto == null) return null;
        SubThread st = new SubThread();
        st.setTitle(dto.getTitle());
        st.setThread(parentThread);
        return st;
    }

    // Message
    public static MessageDto toDto(Message message) {
        if (message == null) return null;
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setBody(message.getBody());
        if (message.getSubThread() != null) {
            try {
                dto.setSubThreadId(message.getSubThread().getId());
            } catch (org.hibernate.LazyInitializationException ignored) {}
        }
        if (message.getUserId() != null && message.getUserId().chars().allMatch(Character::isDigit)) {
            dto.setUserId(Long.parseLong(message.getUserId()));
        }
        dto.setCreatedAt(message.getCreatedAt());
        dto.setUpdatedAt(message.getUpdatedAt());
        dto.setUpvoteCount(message.getUpvoteCount());
        if (message.getTags() != null) {
            dto.setTags(message.getTags().stream().toList());
        }
        return dto;
    }

    public static Message toEntity(MessageDto dto, SubThread subThread) {
        if (dto == null) return null;
        Message message = new Message();
        message.setBody(dto.getBody());
        message.setSubThread(subThread);
        return message;
    }

    // MessageVote
    public static MessageVoteDto toDto(MessageVote vote) {
        if (vote == null) return null;
        MessageVoteDto dto = new MessageVoteDto();
        dto.setMessageId(vote.getMessageId());
        dto.setUserId(vote.getUserId());
        dto.setUpvoted(vote.isUpvoted());
        dto.setCreatedAt(vote.getCreatedAt());
        dto.setUpdatedAt(vote.getUpdatedAt());
        return dto;
    }

    public static MessageVote toEntity(MessageVoteDto dto) {
        if (dto == null) return null;
        MessageVote vote = new MessageVote();
        if (dto.getMessageId() != null || dto.getUserId() != null) {
            vote.setMessageId(dto.getMessageId());
            // userId set in service after user lookup
        }
        vote.setUpvoted(dto.isUpvoted());
        return vote;
    }

    // List mappers
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
