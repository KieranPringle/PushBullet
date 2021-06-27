package com.github.kieranpringle.pushbullet.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason="Failed to send a push")
public class FailedToSendPushException extends Exception{
    public FailedToSendPushException(Exception cause) {
        super("Failed to send push", cause);
    }
}
