package com.ayrotek.forum.service;

import com.ayrotek.forum.entity.Message;
import com.ayrotek.forum.entity.User;
import com.ayrotek.forum.entity.User.Role;
import com.ayrotek.forum.repo.UserRepo;
import com.ayrotek.forum.repo.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.foreign.Linker.Option;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final MessageRepo messageRepo;

    @Autowired
    public UserService(UserRepo userRepo, MessageRepo messageRepo) {
        this.userRepo = userRepo;
        this.messageRepo = messageRepo;
    }

    public User ensureUserExists(String username, String model_id) {
        Optional<User> userOpt = userRepo.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();


            if (model_id != null && !model_id.isBlank()) {
                String current = user.getModel_id();
                if (current == null || current.isBlank()) {
                    user.setModel_id(model_id);
                } else {
                    // Only add if not already present
                    String[] ids = current.split(",");
                    boolean exists = false;
                    for (String id : ids) {
                        if (id.trim().equals(model_id)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        user.setModel_id(current + "," + model_id);
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
            user.setModel_id(model_id);
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
