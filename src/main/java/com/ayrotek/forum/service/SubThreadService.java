package com.ayrotek.forum.service;

import com.ayrotek.forum.entity.SubThread;
import com.ayrotek.forum.entity.Thread;
import com.ayrotek.forum.entity.User;
import com.ayrotek.forum.entity.User.Role;
import com.ayrotek.forum.exception.MissingRelationException;
import com.ayrotek.forum.exception.UserNotFoundException;
import com.ayrotek.forum.repo.SubThreadRepo;
import com.ayrotek.forum.repo.ThreadRepo;
import com.ayrotek.forum.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class SubThreadService {

    private final SubThreadRepo subThreadRepo;
    private final ThreadRepo threadRepo;
    private final UserRepo userRepo;

    public SubThreadService(SubThreadRepo subThreadRepo,
                            ThreadRepo threadRepo,
                            UserRepo userRepo) {
        this.subThreadRepo = subThreadRepo;
        this.threadRepo = threadRepo;
        this.userRepo = userRepo;
    }

    public List<SubThread> getAllSubThreadsByThreadId(Long threadId) {
        // Ensure parent thread exists (avoid count heuristic)
        if (!threadRepo.existsById(threadId)) {
            throw new IllegalArgumentException("Thread not found: " + threadId);
        }
        return subThreadRepo.findByThreadId(threadId);
    }

    public SubThread getSubThreadById(Long id) {
        return subThreadRepo.findById(id).orElse(null);
    }

    private boolean isAdmin(User user) {
        return user != null && user.getRole() == Role.ADMIN;
    }
    private boolean hasModelAccess(User user, String modelId) {
        if (isAdmin(user)) return true;
        return user != null && user.getModelIds() != null && modelId != null && user.getModelIds().contains(modelId);
    }

    public SubThread createSubThread(SubThread subThread, String userIdentifier) {
        if (subThread.getThread() == null || subThread.getThread().getId() == null) {
            throw new MissingRelationException("SubThread must include parent thread");
        }
        Thread parent = threadRepo.findById(subThread.getThread().getId())
                .orElseThrow(() -> new IllegalArgumentException("Parent thread not found: " + subThread.getThread().getId()));
        User user = ensureUserExists(userIdentifier);

        if (!hasModelAccess(user, parent.getModelId())) {
            throw new IllegalStateException("User cannot post to modelId: " + parent.getModelId());
        }
        subThread.setUserId(String.valueOf(user.getId()));
        return subThreadRepo.save(subThread);
    }

    public SubThread updateSubThread(Long subThreadId, SubThread updatedData, String userIdentifier) {
        SubThread existing = subThreadRepo.findById(subThreadId)
                .orElseThrow(() -> new IllegalArgumentException("SubThread not found: " + subThreadId));

        Thread parent = existing.getThread();
        User user = ensureUserExists(userIdentifier);

        if (!isAdmin(user)) {
            if (!String.valueOf(user.getId()).equals(existing.getUserId())) {
                throw new IllegalStateException("User not authorized to modify this subthread");
            }
            if (!hasModelAccess(user, parent.getModelId())) {
                throw new IllegalStateException("User cannot modify modelId: " + parent.getModelId());
            }
        }

        if (updatedData.getTitle() != null) existing.setTitle(updatedData.getTitle());
        // Add other mutable fields if they exist (e.g., content)

        return subThreadRepo.save(existing);
    }

    public void deleteSubThread(Long id, String userIdentifier) {
        SubThread existing = subThreadRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SubThread not found: " + id));

        User user = ensureUserExists(userIdentifier);
        if (!isAdmin(user)) {
            if (!String.valueOf(user.getId()).equals(existing.getUserId())) {
                throw new IllegalStateException("User not authorized to delete this subthread");
            }
        }
        subThreadRepo.delete(existing);
    }

    private User ensureUserExists(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new UserNotFoundException("User identifier blank");
        }
        Optional<User> opt;
        if (isNumeric(identifier)) {
            opt = userRepo.findById(Long.parseLong(identifier));
        } else {
            opt = userRepo.findByUsername(identifier);
        }
        return opt.orElseThrow(() -> new UserNotFoundException("User not found: " + identifier));
    }

    private boolean isNumeric(String s) {
        for (int i = 0; i < s.length(); i++) if (!Character.isDigit(s.charAt(i))) return false;
        return true;
    }
}
