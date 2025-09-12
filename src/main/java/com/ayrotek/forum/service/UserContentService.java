package com.ayrotek.forum.service;

import com.ayrotek.forum.entity.Message;
import com.ayrotek.forum.entity.SubThread;
import com.ayrotek.forum.entity.Thread;
import com.ayrotek.forum.entity.User;
import com.ayrotek.forum.entity.User.Role; // <-- import Role enum
import com.ayrotek.forum.repo.MessageRepo;
import com.ayrotek.forum.repo.SubThreadRepo;
import com.ayrotek.forum.repo.ThreadRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

//This service provides methods to fetch all threads, subthreads, and messages created by a specific user.
@Service
public class UserContentService {

    private final UserService userService;
    private final ThreadRepo threadRepo;
    private final SubThreadRepo subThreadRepo;
    private final MessageRepo messageRepo;

    public UserContentService(UserService userService,
                              ThreadRepo threadRepo,
                              SubThreadRepo subThreadRepo,
                              MessageRepo messageRepo) {
        this.userService = userService;
        this.threadRepo = threadRepo;
        this.subThreadRepo = subThreadRepo;
        this.messageRepo = messageRepo;
    }

    @Transactional(readOnly = true)
    public User requireUser(String username) {
        User u = userService.getUserByUsername(username);
        if (u == null) throw new IllegalArgumentException("User not found: " + username);
        return u;
    }

    private boolean isAdmin(User u) {
        return u != null && u.getRole() == Role.ADMIN;
    }

    @Transactional(readOnly = true)
    public void requireAdminOrSelf(String actingUsername, String targetUsername) {
        User acting = requireUser(actingUsername);
        if (isAdmin(acting)) return;
        if (!actingUsername.equals(targetUsername)) {
            throw new SecurityException("Not authorized to view this user's content");
        }
    }

    // ---- Existing non-paged (self) ----

    @Transactional(readOnly = true)
    public List<Thread> getThreadsByUsername(String username) {
        User u = requireUser(username);
        return threadRepo.findByUserIdOrderByCreatedAtDesc(String.valueOf(u.getId()));
    }

    @Transactional(readOnly = true)
    public List<SubThread> getSubThreadsByUsername(String username) {
        User u = requireUser(username);
        return subThreadRepo.findByUserIdOrderByCreatedAtDesc(String.valueOf(u.getId()));
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesByUsername(String username) {
        User u = requireUser(username);
        return messageRepo.findByUserIdOrderByCreatedAtDesc(String.valueOf(u.getId()));
    }

    // ---- Paged (self) ----

    @Transactional(readOnly = true)
    public Page<Thread> getThreadsByUsername(String username, int page, int size) {
        User u = requireUser(username);
        Pageable pageable = pageReq(page, size, "createdAt");
        return threadRepo.findByUserId(String.valueOf(u.getId()), pageable);
    }

    @Transactional(readOnly = true)
    public Page<SubThread> getSubThreadsByUsername(String username, int page, int size) {
        User u = requireUser(username);
        Pageable pageable = pageReq(page, size, "createdAt");
        return subThreadRepo.findByUserId(String.valueOf(u.getId()), pageable);
    }

    @Transactional(readOnly = true)
    public Page<Message> getMessagesByUsername(String username, int page, int size) {
        User u = requireUser(username);
        Pageable pageable = pageReq(page, size, "createdAt");
        return messageRepo.findByUserId(String.valueOf(u.getId()), pageable);
    }

    // ---- Admin-or-self (target user) non-paged ----

    @Transactional(readOnly = true)
    public List<Thread> getThreadsForUser(String actingUsername, String targetUsername) {
        requireAdminOrSelf(actingUsername, targetUsername);
        return getThreadsByUsername(targetUsername);
    }

    @Transactional(readOnly = true)
    public List<SubThread> getSubThreadsForUser(String actingUsername, String targetUsername) {
        requireAdminOrSelf(actingUsername, targetUsername);
        return getSubThreadsByUsername(targetUsername);
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesForUser(String actingUsername, String targetUsername) {
        requireAdminOrSelf(actingUsername, targetUsername);
        return getMessagesByUsername(targetUsername);
    }

    // ---- Admin-or-self (target user) paged ----

    @Transactional(readOnly = true)
    public Page<Thread> getThreadsForUser(String actingUsername, String targetUsername, int page, int size) {
        requireAdminOrSelf(actingUsername, targetUsername);
        return getThreadsByUsername(targetUsername, page, size);
    }

    @Transactional(readOnly = true)
    public Page<SubThread> getSubThreadsForUser(String actingUsername, String targetUsername, int page, int size) {
        requireAdminOrSelf(actingUsername, targetUsername);
        return getSubThreadsByUsername(targetUsername, page, size);
    }

    @Transactional(readOnly = true)
    public Page<Message> getMessagesForUser(String actingUsername, String targetUsername, int page, int size) {
        requireAdminOrSelf(actingUsername, targetUsername);
        return getMessagesByUsername(targetUsername, page, size);
    }

    // ---- helpers ----

    private Pageable pageReq(int page, int size, String sortProp) {
        int p = Math.max(0, page);
        int s = Math.min(Math.max(1, size), 200);
        return PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, sortProp));
    }
}