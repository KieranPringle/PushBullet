package com.github.kieranpringle.pushbullet.web.rest.user;

import com.github.kieranpringle.pushbullet.domain.User;
import com.github.kieranpringle.pushbullet.exceptions.UserAlreadyExistsException;
import com.github.kieranpringle.pushbullet.repository.UserRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest userToCreate)
            throws UserAlreadyExistsException, URISyntaxException {
        String name = userToCreate.getName();
        log.info("Request to create user : %s", name);

        // leverage URISyntaxException to test if we can store this user
        URI resultUri = new URI(String.format("%s/%s", API_BASE, name));
        User createdUser = repository.addUser(buildUser(userToCreate));

        log.info("User successfully created : %s", name);
        return ResponseEntity.created(resultUri)
                .body(createdUser);
    }

    private User buildUser(CreateUserRequest userToCreate) {
        return new User(
                userToCreate.getName(),
                userToCreate.getAccessToken(),
                Instant.now()
        );
    }
}
