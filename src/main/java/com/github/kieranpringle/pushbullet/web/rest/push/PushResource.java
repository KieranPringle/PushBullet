package com.github.kieranpringle.pushbullet.web.rest.push;

import com.github.kieranpringle.pushbullet.connector.pushbullet.PushBulletConnector;
import com.github.kieranpringle.pushbullet.connector.pushbullet.PushBulletMessage;
import com.github.kieranpringle.pushbullet.domain.User;
import com.github.kieranpringle.pushbullet.exceptions.FailedToSendPushException;
import com.github.kieranpringle.pushbullet.exceptions.NoSuchUserException;
import com.github.kieranpringle.pushbullet.repository.UserRepository;
import com.github.kieranpringle.pushbullet.web.rest.user.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/push")
public class PushResource {
    private final Logger log = LoggerFactory.getLogger(UserResource.class);
    private final UserRepository repository;
    private final PushBulletConnector connector;

    public PushResource(UserRepository repository, PushBulletConnector connector) {
        this.repository = repository;
        this.connector = connector;
    }

    /**
     * POST /messages : post a message to a user
     *
     * Returns the User. Really only because it is a quick and dirty way to show that the count has
     * incremented in the response.
     */
    @PostMapping("/{username}")
    @ResponseStatus(HttpStatus.CREATED)
    public User pushMessage(@PathVariable String username, @RequestBody PushRequest request)
            throws NoSuchUserException, FailedToSendPushException {
        log.info("Request to send message to user : {}", username);
        User user = repository.getUserByName(username);
        if (user == null) {
            log.error("There is no user with the name : {}", username);
            throw new NoSuchUserException(username);
        }

        log.info("User found. Sending message (title: {}, body: {})",
                request.getTitle(), request.getBody());
        connector.sendMessage(buildMessage(request, user));

        log.info("Message send successful, incrementing notification push count for : {}", username);
        user.incrementNotificationsPushed();
        return user;
    }

    private PushBulletMessage buildMessage(PushRequest body, User user) {
        return new PushBulletMessage(body.getTitle(), body.getBody(), user);
    }
}
