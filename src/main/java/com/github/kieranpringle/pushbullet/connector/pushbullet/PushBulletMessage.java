package com.github.kieranpringle.pushbullet.connector.pushbullet;

import com.github.kieranpringle.pushbullet.domain.User;

public class PushBulletMessage {
    private final String title;
    private final String message;
    private final User recipient;

    public PushBulletMessage(String title, String message, User recipient) {
        this.title = title;
        this.message = message;
        this.recipient = recipient;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return message;
    }

    public User getRecipient() {
        return recipient;
    }
}
