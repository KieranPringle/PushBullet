package com.github.kieranpringle.pushbullet.repository;

import com.github.kieranpringle.pushbullet.domain.User;
import com.github.kieranpringle.pushbullet.exceptions.UserAlreadyExistsException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * Adds a simple repository with JPA-like method names. Not persistent.
 *
 * Backed by ConcurrentHashMap so user addition is thread-safe and only one user can be added for
 * each username.
 */
@Service
public class UserRepository {
    private final ConcurrentHashMap<String, User> userRepository = new ConcurrentHashMap<>();

    public Collection<User> getAllUsers() {
        return userRepository.values();
    }

    public User getUserByName(String name) {
        return userRepository.get(name);
    }

    synchronized public User addUser(User user) throws UserAlreadyExistsException {
        String name = user.getName();

        User alreadyStored = userRepository.putIfAbsent(name, user);
        if (alreadyStored != null) {
            throw new UserAlreadyExistsException(name);
        }

        return user;
    }
}
