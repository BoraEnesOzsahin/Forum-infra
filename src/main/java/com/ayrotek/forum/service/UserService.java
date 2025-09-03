package com.ayrotek.forum.service;

import com.ayrotek.forum.entity.User;
import com.ayrotek.forum.entity.User.Role;
import com.ayrotek.forum.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


            if (modelId != null && !modelId.isBlank()) {
                String current = user.getModelId();
                if (current == null || current.isBlank()) {
                    user.setModelId(modelId);
                } else {
                    // Only add if not already present
                    String[] ids = current.split(",");
                    boolean exists = false;
                    for (String id : ids) {
                        if (id.trim().equals(modelId)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        user.setModelId(current + "," + modelId);
                    }
                }

                userRepo.save(user);
            }
            


            return user;
        } else {
            User user = new User();
            user.setUsername(username);
            user.setRole(Role.REGULAR);
            user.setMessage(" ");
            user.setModelId(modelId);
            return userRepo.save(user);
        }
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
