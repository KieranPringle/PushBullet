package com.github.kieranpringle.pushbullet.web.rest.user;

import com.github.kieranpringle.pushbullet.domain.User;
import com.github.kieranpringle.pushbullet.exceptions.InvalidUriException;
import com.github.kieranpringle.pushbullet.exceptions.UserAlreadyExistsException;
import com.github.kieranpringle.pushbullet.repository.UserRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserResource {
    private static final String API_BASE = "/users";

    private final Logger log = LoggerFactory.getLogger(UserResource.class);
    private final UserRepository repository;

    public UserResource(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * POST /users : create a new user
     *
     * Using this API creates a new user
     *
     * @param userToCreate the user to create
     * @return the created user
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody CreateUserRequest userToCreate)
            throws UserAlreadyExistsException, InvalidUriException {
        String name = userToCreate.getName();
        log.info("Request to create user : {}", name);
        testUriCompatibility(name);

        User createdUser = repository.addUser(buildUser(userToCreate));

        log.info("User successfully created : {}", name);
        return createdUser;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return repository.getAllUsers();
    }

    private void testUriCompatibility(String name) throws InvalidUriException {
        try {
            // leverage URISyntaxException to test if we can retrieve this user later
            new URI(String.format("%s/%s", API_BASE, name));
        } catch (URISyntaxException e) {
            throw new InvalidUriException(
                    String.format("Username { %s } contains invalid characters", name),
                    e
            );
        }
    }

    private User buildUser(CreateUserRequest userToCreate) {
        return new User(
                userToCreate.getName(),
                userToCreate.getAccessToken(),
                Instant.now()
        );
    }
}
