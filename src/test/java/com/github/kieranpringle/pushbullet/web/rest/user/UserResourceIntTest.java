package com.github.kieranpringle.pushbullet.web.rest.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kieranpringle.pushbullet.PushbulletApplication;
import com.github.kieranpringle.pushbullet.util.RequestUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.github.kieranpringle.pushbullet.util.RequestUtil.createUserRequest;
import static com.github.kieranpringle.pushbullet.util.RequestUtil.getAllUsersRequest;
import static com.github.kieranpringle.pushbullet.util.RequestUtil.getRequest;
import static com.github.kieranpringle.pushbullet.util.RequestUtil.parseArrayResponse;
import static com.github.kieranpringle.pushbullet.util.RequestUtil.parseResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PushbulletApplication.class)
public class UserResourceIntTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void createAndRetrieveUser() throws Exception {
        String username = UUID.randomUUID().toString();
        String accessToken = UUID.randomUUID().toString();

        String res =  mvc.perform(createUserRequest(username, accessToken))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map created = parseResponse(res);

        assertThat(created.get("name"))
                .as("name should be the same as passed in request")
                .isEqualTo(username);
        assertThat(created.get("accessToken"))
                .as("accessToken should be the same as passed in request")
                .isEqualTo(accessToken);
    }

    @Test
    public void createMultipleUsersAndRetrieveAll() throws Exception {
        String baseUsername = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();

        Collection<String> createdUserNames = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String username = String.format("%s_%d", baseUsername, i);

            mvc.perform(createUserRequest(
                    username,
                    token))
                .andExpect(status().isCreated());

            createdUserNames.add(username);
        }

        String res = mvc.perform(getAllUsersRequest())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Collection allUsers = parseArrayResponse(res);

        for (Object user : allUsers) {
            assertThat(createdUserNames).anySatisfy(name -> {
                assertThat(user.toString()).contains(name);
            });
        }
    }

    @Test
    public void canNotCreateUriIncompatibleUser() throws Exception {
            mvc.perform(createUserRequest("¢#€#", "token"))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void canNotCreateDuplicateUser() throws Exception {
        mvc.perform(createUserRequest("user", "token1"))
                    .andExpect(status().isCreated());

        mvc.perform(createUserRequest("user", "token2"))
                    .andExpect(status().isBadRequest());
    }
}
