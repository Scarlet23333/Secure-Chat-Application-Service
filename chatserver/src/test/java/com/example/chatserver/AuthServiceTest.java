package com.example.chatserver;

import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.chatserver.controllers.AuthService;
import com.example.chatserver.models.User;
import com.example.chatserver.models.UserRepository;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testSaveMessage() {
        // Create a new user
        User user = new User("1", "name", "pswd", "kkk", null);
        
        // Save it to MongoDB
        boolean saved = authService.createUser(user);
        User savedUser = userRepository.findByUserId(user.getUserId());
        
        // Assert the message was saved
        assertTrue(saved); // Mongo generates an ID for saved documents
        assertEquals(user.getUsername(), savedUser.getUsername());
    }
}
