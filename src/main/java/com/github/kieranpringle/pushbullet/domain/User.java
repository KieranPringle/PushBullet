package com.github.kieranpringle.pushbullet.domain;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a User in our application.
 *
 * Since we don't need to be able to update or edit a user's information, beyond incrementing the
 * number of notifications they have received, setters are not provided and these properties of the
 * User class have been made immutable with the `final` keyword. This also helps with thread safety,
 * as we will only have to consider the effect of User#incrementNotificationsPushed on the User
 * object's state. For further thread safety, the counter is implemented with AtomicInteger instead
 * of int.
 */
public class User {
    private final String name;
    private final String accessToken;
    private final Instant creationTime;
    private AtomicInteger numOfNotificationsPushed = new AtomicInteger(0);

    public User(String name, String accessToken, Instant creationTime) {
        this.name = name;
        this.accessToken = accessToken;
        this.creationTime = creationTime;
    }

    public String getName() {
        return name;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public void incrementNotificationsPushed() {
        incrementNotificationsPushed(1);
    }

    public void incrementNotificationsPushed(int times) {
        this.numOfNotificationsPushed.addAndGet(times);
    }

    public int getNumOfNotificationsPushed() {
        return numOfNotificationsPushed.intValue();
    }
}
