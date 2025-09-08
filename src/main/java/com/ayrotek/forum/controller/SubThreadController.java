package com.ayrotek.forum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ayrotek.forum.service.SubThreadService;
import com.ayrotek.forum.service.ThreadService;
import com.ayrotek.forum.entity.SubThread;
import com.ayrotek.forum.entity.Thread;
import com.ayrotek.forum.dto.SubThreadDto;
import com.ayrotek.forum.dto.DtoMapper;
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

    @GetMapping("/allSubThreadsByThreadId/{threadId}")
    public ResponseEntity<ServerResponse> getAllSubThreads(@PathVariable Long threadId) {
        try {
            List<SubThread> subThreads = subThreadService.getAllSubThreadsByThreadId(threadId);
            return ResponseEntity.ok(new ServerResponse(true, "SubThreads fetched successfully", DtoMapper.toSubThreadDtoList(subThreads)));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerResponse(false, "Error fetching subthreads: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServerResponse> getSubThreadById(@PathVariable Long id) {
        try {
            SubThread subThread = subThreadService.getSubThreadById(id);
            if (subThread == null) {
                return ResponseEntity.status(404).body(new ServerResponse(false, "SubThread not found", null));
            }
            return ResponseEntity.ok(new ServerResponse(true, "SubThread fetched successfully", DtoMapper.toDto(subThread)));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerResponse(false, "Error fetching subthread: " + e.getMessage(), null));
        }
    }

    @PostMapping("/createSubThread")
    public ResponseEntity<ServerResponse> createSubThread(@RequestBody SubThreadDto subThreadDto) {
        try {
            Thread thread = threadService.getThreadById(subThreadDto.getThreadId());
            if (thread == null) {
                return ResponseEntity.status(404).body(new ServerResponse(false, "Thread not found", null));
            }
            SubThread subThread = DtoMapper.toEntity(subThreadDto, thread);
            SubThread saved = subThreadService.createSubThread(subThread);
            return ResponseEntity.ok(new ServerResponse(true, "SubThread created successfully", DtoMapper.toDto(saved)));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerResponse(false, "Error creating subthread: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/deleteSubThread/{id}")
    public ResponseEntity<ServerResponse> deleteSubThread(@PathVariable Long id) {
        try {
            subThreadService.deleteSubThread(id);
            return ResponseEntity.ok(new ServerResponse(true, "SubThread deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerResponse(false, "Error deleting subthread: " + e.getMessage(), null));
        }
    }
}
