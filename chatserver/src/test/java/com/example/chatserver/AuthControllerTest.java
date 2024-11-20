package com.example.chatserver;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.chatserver.controllers.AuthService;
import com.example.chatserver.models.User;


@WebMvcTest(AuthControllerTest.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        // Setup sample data for testing
        user1 = new User("1", "user1", "user1", null, null);
        user2 = new User("2", "user2", "user2", null, null);


        // When service method is called, return the prepared messages
        when(authService.createUser(user1)).thenReturn(true);
    }

    @Test
    public void testUserIdAuth() throws Exception {
        // String username = "test_user";
        // when(authService.userIdAuth(username))
        //         .thenReturn(List.of(new Message("1", username, "Hello")));

        mockMvc.perform(post("/api/auth/user")
                .contentType("application/json")
                .content("{\"userId\" : \"1\", \"username\": \"user1\", \"password\": \"user1\", \"publicKey\": \"\", \"profile\": \"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("1"));
    }
}
