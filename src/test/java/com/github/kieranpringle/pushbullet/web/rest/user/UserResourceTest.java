package com.github.kieranpringle.pushbullet.web.rest.user;

import com.github.kieranpringle.pushbullet.domain.User;
import com.github.kieranpringle.pushbullet.exceptions.InvalidUriException;
import com.github.kieranpringle.pushbullet.exceptions.UserAlreadyExistsException;
import com.github.kieranpringle.pushbullet.repository.UserRepository;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit (not integration!) tests for UserResource
 */
class UserResourceTest {

    private static final String USERNAME = "username";
    private static final String TOKEN = "accessToken";

    private UserRepository mockRepository;
    private User mockUser;

    @Captor
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

    private UserResource resource;

    @BeforeEach
    public void setUp() {
        mockRepository = mock(UserRepository.class);
        mockUser = mock(User.class);
        resource = new UserResource(mockRepository);
    }

    @Test
    public void createUserWithCorrectParams()
            throws UserAlreadyExistsException, InvalidUriException {
        CreateUserRequest req = buildRequest();

        when(mockRepository.addUser(userCaptor.capture()))
                .thenReturn(mockUser);

        resource.createUser(req);

        User requestedUser = userCaptor.getValue();

        assertThat(requestedUser.getName())
                .as("Has the correct username")
                .isEqualTo(USERNAME);
        assertThat(requestedUser.getAccessToken())
                .as("Has the correct username")
                .isEqualTo(TOKEN);
        assertThat(requestedUser.getCreationTime())
                .as("Was created recently")
                .isBetween(Instant.now().minusMillis(10000), Instant.now());
    }

    @Test
    public void returnCreatedUser()
            throws UserAlreadyExistsException, InvalidUriException {
        CreateUserRequest req = buildRequest();

        when(mockRepository.addUser(any(User.class)))
                .thenReturn(mockUser);

        User res = resource.createUser(req);

        assertThat(res)
                .as("Request should return created user")
                .isEqualTo(mockUser);
    }

    @Test
    public void doesNotAttemptToCreateAUriIncompatibleUser() throws UserAlreadyExistsException {
        CreateUserRequest req = buildRequest("&^£@");

        assertThatThrownBy(() -> resource.createUser(req))
                .isOfAnyClassIn(InvalidUriException.class);

        verify(mockRepository, never()).addUser(any());
    }


    private CreateUserRequest buildRequest() {
        return buildRequest(USERNAME);
    }

    private CreateUserRequest buildRequest(String name) {
        CreateUserRequest req = new CreateUserRequest();
        req.setName(name);
        req.setAccessToken(TOKEN);
        return req;
    }
}
