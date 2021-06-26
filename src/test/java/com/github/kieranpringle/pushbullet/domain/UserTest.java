package com.github.kieranpringle.pushbullet.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private final String name = "username";
    private final String accessToken = "aCc3s5t0k3n";
    private final Instant creationTime = Instant.EPOCH;

    private User user;

    @BeforeEach
    void setUp() {
        this.user = new User(
            name,
            accessToken,
            creationTime
        );
    }

    @Test
    public void hasExpectedName() {
        assertThat(user.getName()).isEqualTo(name);
    }

    @Test
    public void hasExpectedAccessToken() {
        assertThat(user.getAccessToken()).isEqualTo(accessToken);
    }

    @Test
    public void hasExpectedCreationTime() {
        assertThat(user.getCreationTime()).isEqualTo(creationTime);
    }

    @Test
    public void isInitialisedWithZeroNotificationsPushed() {
        assertThat(user.getNumOfNotificationsPushed()).isEqualTo(0);
    }

    @Test
    public void canIncrementNotificationsPushedByOne() {
        int pushed = user.getNumOfNotificationsPushed();

        user.incrementNotificationsPushed();

        assertThat(user.getNumOfNotificationsPushed()).isEqualTo(pushed + 1);
    }

    @Test
    public void canIncrementNotificationsPushedByMany() {
        int pushed = user.getNumOfNotificationsPushed();

        user.incrementNotificationsPushed(3);

        assertThat(user.getNumOfNotificationsPushed()).isEqualTo(pushed + 3);
    }

    @Test
    public void incrementationIsThreadSafe() throws InterruptedException {
        int threads = 10;
        ExecutorService service =
                Executors.newFixedThreadPool(threads);
        Collection<Callable<Void>> runnables = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            runnables.add(() -> {
                user.incrementNotificationsPushed();
                return null;
            });
        }
        service.invokeAll(runnables);

        assertThat(user.getNumOfNotificationsPushed()).isEqualTo(threads);
    }
}
