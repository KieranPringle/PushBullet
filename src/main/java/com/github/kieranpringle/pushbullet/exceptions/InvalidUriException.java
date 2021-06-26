package com.github.kieranpringle.pushbullet.exceptions;


import java.net.URISyntaxException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Contains invalid characters for a URI")
public class InvalidUriException extends Exception {
    public InvalidUriException(String message, URISyntaxException cause) {
        super(message, cause);
    }
}
