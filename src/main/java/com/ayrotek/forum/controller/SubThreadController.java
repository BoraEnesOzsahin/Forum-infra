package com.ayrotek.forum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ayrotek.forum.service.SubThreadService;
import com.ayrotek.forum.service.ThreadService;
import com.ayrotek.forum.entity.SubThread;
import com.ayrotek.forum.entity.Thread;
import com.ayrotek.forum.dto.SubThreadDto;
import com.ayrotek.forum.dto.DtoMapper;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.ayrotek.forum.entity.ServerResponse;

@RestController
@RequestMapping("/subThreads")

public class SubThreadController {
    private final SubThreadService subThreadService;
    private final ThreadService threadService;

    @Autowired
    public SubThreadController(SubThreadService subThreadService, ThreadService threadService) {
        this.subThreadService = subThreadService;
        this.threadService = threadService;
    }

    @GetMapping("/allThreadsByThreadId/{threadId}")
    public ServerResponse getAllSubThreads(@PathVariable Long threadId) {
        List<SubThread> subThreads = subThreadService.getAllSubThreadsByThreadId(threadId);
        return new ServerResponse(true, "SubThreads fetched successfully", DtoMapper.toSubThreadDtoList(subThreads));
    }


    @GetMapping("/{id}")
    public ServerResponse getSubThreadById(@PathVariable Long id) {
        SubThread subThread = subThreadService.getSubThreadById(id);
        if (subThread == null) {
            return new ServerResponse(false, "SubThread not found", null);
        }
        return new ServerResponse(true, "SubThread fetched successfully", DtoMapper.toDto(subThread));
    }


    @PostMapping("/createSubThread")
    public ServerResponse createSubThread(@RequestBody SubThreadDto subThreadDto) {
        Thread thread = threadService.getThreadById(subThreadDto.getThreadId());
        SubThread subThread = DtoMapper.toEntity(subThreadDto, thread);
        SubThread saved = subThreadService.createSubThread(subThread);
        return new ServerResponse(true, "SubThread created successfully", DtoMapper.toDto(saved));
    }

    @DeleteMapping("/deleteSubThread/{id}")
    public ServerResponse deleteSubThread(@PathVariable Long id) {
        subThreadService.deleteSubThread(id);
        return new ServerResponse(true, "SubThread deleted successfully", null);
    }

}
