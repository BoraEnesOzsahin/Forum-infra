package com.ayrotek.forum.service;

import com.ayrotek.forum.entity.User;
import com.ayrotek.forum.exception.IdMismatchException;
import com.ayrotek.forum.exception.UserNotFoundException;
import com.ayrotek.forum.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ayrotek.forum.entity.User.Role;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User ensureUserExists(String username, String modelId) {
        Optional<User> userOpt = userRepo.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // If the user is an ADMIN, bypass all model_id checks 
            if (user.getRole() == Role.ADMIN) {
                return user; // Admin can do anything, validation successful.
            }

            // --- For CITIZEN roles, the existing validation applies ---

            // Check if user has a model_id
            String userModelId = user.getModelId();
            if (userModelId == null || userModelId.isBlank()) {
                throw new UserNotFoundException("User '" + username + "' does not have a model_id assigned. Cannot perform operation.");
            }

            // Check if user's model_id matches the required modelId
            if (modelId != null && !modelId.isBlank()) {
                if (!userModelId.equals(modelId)) {
                    throw new IdMismatchException("User '" + username + "' has model_id '" + userModelId + "' but this operation requires model_id '" + modelId + "'.");
                }
            }

            return user;
        } else {
            throw new UserNotFoundException("User with username '" + username + "' not found. Cannot perform operation.");
        }
    }

    public User getUserByUsername(String username) {
        return userRepo.findByUsername(username).orElse(null);
    }

    public void addMessageToUser(String username, String messageCont){
        userRepo.findByUsername(username).ifPresent(user -> {
            String currentMessage = user.getMessage();
            if (currentMessage == null || currentMessage.isBlank()) {
                user.setMessage(messageCont);
            } else {
                user.setMessage(currentMessage + "\n" + messageCont);
            }
            userRepo.save(user);
        });
    }
}
