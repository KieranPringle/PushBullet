package com.github.kieranpringle.pushbullet.web.rest.push;

import com.github.kieranpringle.pushbullet.PushbulletApplication;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assumptions;
import org.junit.platform.commons.util.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static com.github.kieranpringle.pushbullet.util.RequestUtil.createUserRequest;
import static com.github.kieranpringle.pushbullet.util.RequestUtil.getAllUsersRequest;
import static com.github.kieranpringle.pushbullet.util.RequestUtil.parseArrayResponse;
import static com.github.kieranpringle.pushbullet.util.RequestUtil.parseResponse;
import static com.github.kieranpringle.pushbullet.util.RequestUtil.pushRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PushbulletApplication.class)
public class PushResourceIntTest {
    // these can be set manually, but in the real world we would probably use config that let us
    // point at a WireMock server and/or read these in from the env
    private static final String USERNAME = "test_user";
    private static final String ACCESS_TOKEN = "";
    private static final int MESSAGES = 3;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @Ignore
    public void pushMessagesToUserOnMultipleThreads() throws Exception {
        Assumptions.assumeTrue(
                StringUtils.isNotBlank(ACCESS_TOKEN),
                "You need to hard code your own access token for this test");

        createTestUser();
        sendPushMessages();
        Map createdUser = findCreatedUser(getAllUsers());

        assertThat(Integer.parseInt(createdUser.get("numOfNotificationsPushed").toString()))
                .isEqualTo(MESSAGES);
    }

    private Collection getAllUsers() throws Exception {
        String allUsersResponse = mvc.perform(getAllUsersRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return parseArrayResponse(allUsersResponse);
    }

    private void sendPushMessages() throws Exception {
        ExecutorService service =
                Executors.newFixedThreadPool(MESSAGES);
        Collection<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < MESSAGES; i++) {
            futures.add(
                    service.submit(() -> {
                        try {
                            MockHttpServletRequestBuilder req = pushRequest(
                                    USERNAME,
                                    String.format("test message %s", UUID.randomUUID().toString()),
                                    String.format("message body %s", UUID.randomUUID().toString()));
                            mvc.perform(req)
                                    .andExpect(status().isCreated());
                        } catch (Exception ignored) {
                        }
                    })
            );
        }

        for (Future<?> f : futures) {
            f.get();
        }
    }

    private void createTestUser() throws Exception {
        MockHttpServletResponse createUserRes = mvc.perform(createUserRequest(USERNAME, ACCESS_TOKEN))
                .andReturn().getResponse();
        assertThat(createUserRes.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    private Map findCreatedUser(Collection allUsers) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map result = null;
        for (Object u : allUsers) {
            Map userMap = parseResponse(mapper.writeValueAsString(u));
            if (!userMap.get("name").toString().equals(USERNAME)) {
                continue;
            }
            // check the sent messages count
            result = userMap;
        }
        return result;
    }
}
