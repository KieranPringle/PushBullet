package com.github.kieranpringle.pushbullet.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Simple exception class we can map to a suitable HTTP error code
 */
@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="User already exists")
public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String name) {
        super(String.format("Could not create user <%s>, username is already taken", name));
    }
}
