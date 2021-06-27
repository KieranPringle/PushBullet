package com.github.kieranpringle.pushbullet.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kieranpringle.pushbullet.web.rest.push.PushRequest;
import com.github.kieranpringle.pushbullet.web.rest.user.CreateUserRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class RequestUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static MockHttpServletRequestBuilder createUserRequest(String name, String token)
            throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setName(name);
        req.setAccessToken(token);

        return postRequest("/users", req);
    }

    public static MockHttpServletRequestBuilder getAllUsersRequest() throws Exception {
        return getRequest("/users");
    }

    public static MockHttpServletRequestBuilder pushRequest(
            String username, String title, String body)
            throws Exception {
        PushRequest req = new PushRequest();
        req.setTitle(title);
        req.setBody(body);

        return postRequest(
                String.format("/push/%s", username),
                req);
    }

    public static MockHttpServletRequestBuilder postRequest(String path, Object body) throws IOException {
        return post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(convertToJsonBytes(body));
    }

    public static MockHttpServletRequestBuilder getRequest(String path) throws IOException {
        return get(path)
                .accept(MediaType.APPLICATION_JSON);
    }

    public static Map parseResponse(String body) throws JsonProcessingException {
        return MAPPER.readValue(body, Map.class);
    }

    public static List parseArrayResponse(String body) throws JsonProcessingException {
        return MAPPER.readValue(body, List.class);
    }

    private static byte[] convertToJsonBytes(Object object)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        return mapper.writeValueAsBytes(object);
    }
}
