package com.github.kieranpringle.pushbullet.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class RequestUtil {

    public static MockHttpServletRequestBuilder postRequest(String path, Object body) throws IOException {
        return post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJsonBytes(body));
    }

    private static byte[] convertToJsonBytes(Object object)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        return mapper.writeValueAsBytes(object);
    }
}
