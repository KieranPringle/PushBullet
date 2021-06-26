package com.github.kieranpringle.pushbullet.web.rest.user;

/**
 * Represents the expected request body when a user is created.
 *
 * It is bad practice to directly parse an object passed in an HTTP request and put it straight into
 * your DB. We also do not want User#creationTime or User#numberOfNotificationsPushed to be
 * available to be manually set.
 */
public class CreateUserRequest {
    private String name;
    private String accessToken;

    public CreateUserRequest(String name, String accessToken) {
        this.name = name;
        this.accessToken = accessToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
