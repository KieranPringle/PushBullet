package com.github.kieranpringle.pushbullet.exceptions;

/**
 * Simple exception class we can map to a suitable HTTP error code
 */
public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String name) {
        super(String.format("Could not create user <%s>, username is already taken", name));
    }
}
