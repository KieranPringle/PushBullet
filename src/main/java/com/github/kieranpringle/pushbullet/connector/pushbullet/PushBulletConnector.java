package com.github.kieranpringle.pushbullet.connector.pushbullet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kieranpringle.pushbullet.exceptions.FailedToSendPushException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;


/**
 * https://docs.pushbullet.com/#create-push
 *
 * This class is based around the Apache HttpClient so we can mock it out and test this class
 * without actually calling PushBullet. In practice, you shouldn't mock classes you don't own, and
 * it would be better to inject some other class, even if that was build around HttpClient, or to
 * use something like WireMock and fancy Spring config at test time to make calls to a fake server.
 * Same stands for ObjectMapper, which would likely be wrapped up in this imaginary other class.
 */
@Service
public class PushBulletConnector {
    // real world, this URI would come from config
    private static final String PUSH_BULLET_CREATE_PUSH_URL = "https://api.pushbullet.com/v2/pushes";
    private static final String PUSH_TYPE = "note";

    private final HttpClient client;
    private final ObjectMapper mapper;

    PushBulletConnector(HttpClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    public void sendMessage(PushBulletMessage message) throws FailedToSendPushException {
        try {
            HttpRequest req = buildRequest(message);
            client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            // in practice, catching all exceptions is a terrible idea!
            throw new FailedToSendPushException(e);
        }
    }

    private HttpRequest buildRequest(PushBulletMessage message)
            throws JsonProcessingException, URISyntaxException {

        return HttpRequest.newBuilder()
                .header("Access-Token", message.getRecipient().getAccessToken())
                .header("Content-Type", MediaType.APPLICATION_JSON.toString())
                .POST(buildJsonBody(message))
                .uri(new URI(PUSH_BULLET_CREATE_PUSH_URL))
                .build();
    }

    private HttpRequest.BodyPublisher buildJsonBody(PushBulletMessage message)
            throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        map.put("type", PUSH_TYPE);
        map.put("title", message.getTitle());
        map.put("body", message.getBody());

        return HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(map));
    }
}
