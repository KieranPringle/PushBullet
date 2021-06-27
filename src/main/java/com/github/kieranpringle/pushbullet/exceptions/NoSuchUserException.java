package com.github.kieranpringle.pushbullet.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No user with this name")
public class NoSuchUserException extends Exception {
    public NoSuchUserException(String name) {
        super(String.format("There is no user with the name : %s", name));
    }
}
