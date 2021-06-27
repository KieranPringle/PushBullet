package com.github.kieranpringle.pushbullet.connector.pushbullet;

import com.github.kieranpringle.pushbullet.domain.User;

public class PushBulletMessage {
    private final String title;
    private final String message;
    private final User user;

    public PushBulletMessage(String title, String message, User user) {
        this.title = title;
        this.message = message;
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return message;
    }

    public User getUser() {
        return user;
    }
}
