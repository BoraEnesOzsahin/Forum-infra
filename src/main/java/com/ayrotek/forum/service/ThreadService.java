package com.ayrotek.forum.service;

import com.ayrotek.forum.entity.Thread;
import com.ayrotek.forum.entity.User;
import com.ayrotek.forum.entity.User.Role;
import com.ayrotek.forum.exception.UserNotFoundException;
import com.ayrotek.forum.repo.ThreadRepo;
import com.ayrotek.forum.repo.UserRepo;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThreadService {

    private final ThreadRepo threadRepo;
    private final UserRepo userRepo;

    @Autowired
    public ThreadService(ThreadRepo threadRepo, UserRepo userRepo) {
        this.threadRepo = threadRepo;
        this.userRepo = userRepo;
    }

    public Thread getThreadById(Long id) {
        return threadRepo.findById(id).orElse(null);
    }

    public Thread createThread(Thread thread, String userIdentifier) {
        User user = ensureUserExists(userIdentifier);
        enforceModelIdIfRequired(user);

        if (isAdmin(user)) {
            // Admin: accept provided modelId (can be null)
        } else {
            // Non-admin: force thread.modelId = user's modelId; reject mismatch if client tried to spoof
            if (thread.getModelId() != null && !thread.getModelId().equals(user.getModelId())) {
                throw new IllegalStateException("modelId mismatch: user not allowed to create thread for another model");
            }
            thread.setModelId(thread.getModelId()); // keep requested model
        }

        thread.setUserId(String.valueOf(user.getId()));
        return threadRepo.save(thread);
    }

    public Thread updateExistingThread(Thread existing, Thread updatedData, String userIdentifier) {
        User user = ensureUserExists(userIdentifier);
        enforceModelIdIfRequired(user);

        // Ownership or admin handled here
        if (!isAdmin(user)) {
            if (!String.valueOf(user.getId()).equals(existing.getUserId())) {
                throw new IllegalStateException("User not authorized to modify this thread");
            }
            // Prevent modelId change
            if (updatedData.getModelId() != null && !updatedData.getModelId().equals(existing.getModelId())) {
                throw new IllegalStateException("Non-admin cannot change modelId");
            }
        } else {
            // Admin may change modelId
            if (updatedData.getModelId() != null) {
                existing.setModelId(updatedData.getModelId());
            }
        }

        // Apply other mutable fields (adjust to your fields)
        if (updatedData.getTitle() != null) existing.setTitle(updatedData.getTitle());

        return threadRepo.save(existing);
    }

    public void deleteThread(Long id) {
        threadRepo.deleteById(id);
    }

    public List<Thread> getAllThreads() {
        return threadRepo.findAll();
    }

    // --- New helper methods ---

    public User ensureUserExists(String userIdentifier) {
        if (userIdentifier == null || userIdentifier.isBlank()) {
            throw new UserNotFoundException("User identifier is null/blank");
        }
        Optional<User> userOpt;
        if (isNumeric(userIdentifier)) {
            userOpt = userRepo.findById(Long.parseLong(userIdentifier));
        } else {
            userOpt = userRepo.findByUsername(userIdentifier);
        }
        return userOpt.orElseThrow(() ->
                new UserNotFoundException("User not found: " + userIdentifier));
    }

    public void assertUserCanModifyThread(Long threadId, String userIdentifier) {
        Thread thread = threadRepo.findById(threadId)
                .orElseThrow(() -> new IllegalStateException("Thread not found: " + threadId));
        User acting = ensureUserExists(userIdentifier);
        if (isAdmin(acting)) return;
        enforceModelIdIfRequired(acting);             // ensures non-admin still has modelId
        if (!String.valueOf(acting.getId()).equals(thread.getUserId())) {
            throw new IllegalStateException("User not authorized to modify this thread");
        }
    }

    private boolean isAdmin(User user) {
        return user != null && user.getRole() == Role.ADMIN;
    }

    private boolean hasModelAccess(User user, String modelId) {
        if (isAdmin(user)) return true;
        return user != null && user.getModelIds() != null && modelId != null && user.getModelIds().contains(modelId);
    }

    private void enforceModelIdIfRequired(User user) {
        if (isAdmin(user)) return; // admin bypass
        if (user.getModelId() == null || user.getModelId().isBlank()) {
            throw new IllegalStateException("modelId required for this action");
        }
    }

    // OLD METHOD (can be removed if unused elsewhere)
    // public void assertUserOwnsThread(...)

    private boolean isNumeric(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }
}

