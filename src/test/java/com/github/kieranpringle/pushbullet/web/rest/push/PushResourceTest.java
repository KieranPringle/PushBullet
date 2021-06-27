package com.github.kieranpringle.pushbullet.web.rest.push;

import com.github.kieranpringle.pushbullet.connector.pushbullet.PushBulletConnector;
import com.github.kieranpringle.pushbullet.connector.pushbullet.PushBulletMessage;
import com.github.kieranpringle.pushbullet.domain.User;
import com.github.kieranpringle.pushbullet.exceptions.FailedToSendPushException;
import com.github.kieranpringle.pushbullet.exceptions.NoSuchUserException;
import com.github.kieranpringle.pushbullet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PushResourceTest {
    private final static String USERNAME = "auserpersonwithaname";
    private final static String TITLE = "BREAKING NEWS";
    private final static String BODY = "something topical";

    private UserRepository mockRepository;
    private PushBulletConnector mockConnector;
    private User mockUser;

    ArgumentCaptor<PushBulletMessage> msgCaptor = ArgumentCaptor.forClass(PushBulletMessage.class);

    private PushResource pushResource;

    @BeforeEach
    public void setUp() {
        mockRepository = mock(UserRepository.class);
        mockConnector = mock(PushBulletConnector.class);
        mockUser = mock(User.class);

        pushResource = new PushResource(mockRepository, mockConnector);

        when(mockRepository.getUserByName(matches(USERNAME)))
                .thenReturn(mockUser);
    }

    @Test
    public void sendsMessageToUser() throws FailedToSendPushException, NoSuchUserException {
        doNothing().when(mockConnector).sendMessage(msgCaptor.capture());


        pushResource.pushMessage(USERNAME, buildRequest());

        PushBulletMessage sentMessage = msgCaptor.getValue();
        assertThat(sentMessage.getBody())
                .as("Message body should be same as passed")
                .isEqualTo(BODY);
        assertThat(sentMessage.getTitle())
                .as("Message title should be same as passed")
                .isEqualTo(TITLE);
        assertThat(sentMessage.getRecipient())
                .as("Message recipient should match the user returned from repository")
                .isEqualTo(mockUser);
    }

    @Test
    public void incrementsNotificationsPushedOnSuccess()
            throws FailedToSendPushException, NoSuchUserException {
        pushResource.pushMessage(USERNAME, buildRequest());

        verify(mockUser, times(1)).incrementNotificationsPushed();
    }

    @Test
    public void returnsTheUserTheMessageWasSentTo() throws FailedToSendPushException, NoSuchUserException {
        User user = pushResource.pushMessage(USERNAME, buildRequest());

        assertThat(user).isEqualTo(mockUser);
    }

    @Test
    public void doesNotSendAMessageIfUserDoesNotExist()
            throws FailedToSendPushException, NoSuchUserException {
        when(mockRepository.getUserByName(matches(USERNAME)))
                .thenReturn(null);

        assertThatThrownBy(() -> pushResource.pushMessage(USERNAME, buildRequest()))
                .isOfAnyClassIn(NoSuchUserException.class);
    }

    @Test
    public void doesNotIncrementNotificationsPushedOnFailure()
            throws FailedToSendPushException {
        doThrow(FailedToSendPushException.class).when(mockConnector).sendMessage(any());

        assertThatThrownBy(() -> pushResource.pushMessage(USERNAME, buildRequest()))
                .isOfAnyClassIn(FailedToSendPushException.class);

        verify(mockUser, never()).incrementNotificationsPushed();
    }

    private PushRequest buildRequest() {
        PushRequest req = new PushRequest();
        req.setTitle(TITLE);
        req.setBody(BODY);

        return req;
    }
}
