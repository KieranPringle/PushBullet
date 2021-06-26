package com.github.kieranpringle.pushbullet.repository;

import com.github.kieranpringle.pushbullet.domain.User;
import com.github.kieranpringle.pushbullet.exceptions.UserAlreadyExistsException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserRepositoryTest {

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
    }

    @Test
    public void canAddAndRetrieveUser() throws UserAlreadyExistsException {
        String username = "username";
        User storedUser = user(username);

        userRepository.addUser(storedUser);
        User retrievedUser = userRepository.getUserByName(username);

        assertThat(retrievedUser).isSameAs(storedUser);
    }

    @Test
    public void canAddMultipleUsersAndRetrieveAll() throws UserAlreadyExistsException {
        User user1 = user("user1");
        User user2 = user("user2");
        User user3 = user("user3");

        userRepository.addUser(user1);
        userRepository.addUser(user2);
        userRepository.addUser(user3);

        Collection<User> allUsers = userRepository.getAllUsers();

        assertThat(allUsers).contains(user1, user2, user3);
    }

    @Test
    public void canNotAddDuplicateUser() throws UserAlreadyExistsException {
        String username = "username";
        User user = user(username);

        userRepository.addUser(user);

        assertThatThrownBy(() -> userRepository.addUser(user))
                .isOfAnyClassIn(UserAlreadyExistsException.class)
                .hasMessageContaining(username);
    }

    @Test
    public void userAdditionIsThreadSafe()
            throws InterruptedException, ExecutionException {
        User user = user("duplicateUser");

        int threads = 10;
        ExecutorService service =
                Executors.newFixedThreadPool(threads);
        Collection<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            futures.add(
                service.submit(() -> {
                    boolean success = false;
                    try {
                        userRepository.addUser(user);
                        success = true;
                    } catch (UserAlreadyExistsException ignored) { }
                    return success;
                })
            );
        }

        int total = 0;
        for (Future<Boolean> f : futures) {
            if (f.get()) {
                total += 1;
            }
        }

        assertThat(total)
                .as("Only one attempt to add the user should succeed")
                .isEqualTo(1);
    }

    private User user(String name) {
        return new User(
                name,
                UUID.randomUUID().toString(),
                Instant.now());
    }
}
