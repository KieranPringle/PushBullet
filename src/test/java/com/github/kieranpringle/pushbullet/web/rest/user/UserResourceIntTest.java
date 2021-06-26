package com.github.kieranpringle.pushbullet.web.rest.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kieranpringle.pushbullet.PushbulletApplication;
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

import static com.github.kieranpringle.pushbullet.util.RequestUtil.postRequest;
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

        String res = createUserRequest(username, accessToken)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map created = responseBodyToUser(res);

        assertThat(created.get("name"))
                .as("name should be the same as passed in request")
                .isEqualTo(username);
        assertThat(created.get("accessToken")).isEqualTo(accessToken)
                .as("accessToken should be the same as passed in request")
                .isEqualTo(accessToken);
    }

    @Test
    public void createMultipleUsersAndRetrieveAll() throws Exception {
        String baseUsername = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();

        Collection<String> createdUsers = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String username = String.format("%s_%d", baseUsername, i);

            createUserRequest(
                    username,
                    token)
                .andExpect(status().isCreated());

            createdUsers.add(username);
        }

        // to implement `GET /users`
    }

    @Test
    public void canNotCreateUriIncompatibleUser() throws Exception {
        //    createUserRequest("¢#€#", "token")
        //            .andExpect(status().isCreated());

        // to implement: proper exception mapping
    }

    @Test
    public void canNotCreateDuplicateUser() {
        //    createUserRequest("user", "token1")
        //            .andExpect(status().isCreated());

        //    createUserRequest("user", "token2")
        //            .andExpect(status().isCreated());

        // to implement: proper exception mapping
    }

    private Map responseBodyToUser(String body) throws JsonProcessingException {
        return MAPPER.readValue(body, Map.class);
    }

    private ResultActions createUserRequest(String name, String token) throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setName(name);
        req.setAccessToken(token);

        return mvc.perform(postRequest("/users", req));
    }
}
