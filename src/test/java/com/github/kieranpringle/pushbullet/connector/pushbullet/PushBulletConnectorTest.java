package com.github.kieranpringle.pushbullet.connector.pushbullet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kieranpringle.pushbullet.domain.User;
import com.github.kieranpringle.pushbullet.exceptions.FailedToSendPushException;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Instant;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PushBulletConnectorTest {

    private HttpClient mockClient;
    private ObjectMapper mockMapper;

    private ArgumentCaptor<Map> bodyCaptor = ArgumentCaptor.forClass(Map.class);
    private ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);

    private PushBulletConnector connector;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        this.mockClient = mock(HttpClient.class);
        this.mockMapper = mock(ObjectMapper.class);

        this.connector = new PushBulletConnector(mockClient, mockMapper);

        when(mockMapper.writeValueAsString(any())).thenReturn("{}");
    }

    @Test
    public void buildsBodyFromMessage() throws Exception {
        when(mockMapper.writeValueAsString(bodyCaptor.capture()))
                .thenReturn("{}");

        connector.sendMessage(getMsg());

        Map body = bodyCaptor.getValue();

        assertThat(body.get("type")).isEqualTo("note");
        assertThat(body.get("title")).isEqualTo("title");
        assertThat(body.get("body")).isEqualTo("body");
    }

    @Test
    public void postsToPushBulletWithAccessToken() throws Exception {
        when(mockClient.send(requestCaptor.capture(), any())).thenReturn(null);

        connector.sendMessage(getMsg());

        HttpRequest req = requestCaptor.getValue();

        assertThat(req.uri().toString()).isEqualTo("https://api.pushbullet.com/v2/pushes");
        assertThat(req.method()).isEqualTo("POST");
        assertThat(req.headers().firstValue("Access-Token").get()).isEqualTo("token");
    }

    @Test
    public void convertsExceptionOnFailureToSend() throws Exception {
        when(mockClient.send(requestCaptor.capture(), any())).thenThrow(new IOException());

        Assertions.assertThatThrownBy(() -> connector.sendMessage(getMsg()))
                .isOfAnyClassIn(FailedToSendPushException.class);
    }

    private PushBulletMessage getMsg() {
        User usr = new User("user", "token", Instant.now());
        return new PushBulletMessage(
                "title",
                "body",
                usr
        );
    }
}
