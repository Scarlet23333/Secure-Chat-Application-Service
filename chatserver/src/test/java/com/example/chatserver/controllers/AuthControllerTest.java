package com.example.chatserver.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.chatserver.models.User;
import com.example.chatserver.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private SimpMessagingTemplate messagingTemplate;
    
    @InjectMocks
    private AuthController authController;

    @Autowired
    private ObjectMapper objectMapper; // To convert object to JSON

    private User user1, user2;

    @BeforeEach
    void setup() {
        // Setup sample data for testing
        user1 = new User("1", "user1", "user1", "1", "");
        user2 = new User("2", "user2", "user2", "2", "");
    }

    @Test
    public void testRegisterUser() throws Exception {
        when(authService.createUser(user1)).thenReturn(true);

        // Act and Assert
        mockMvc.perform(post("/api/auth/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Create-User-UserId", user1.getUserId()))
                .andExpect(content().string("User registered."));
        
        verify(authService).createUser(user1);

        when(authService.createUser(user1)).thenReturn(false);

        mockMvc.perform(post("/api/auth/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User id existed."));

    }

    @Test
    public void testLogin() throws Exception {
        String userId = user1.getUserId(), password = user1.getPassword();
        when(authService.login(userId, password)).thenReturn(user1);

        mockMvc.perform(get("/api/auth/login/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(password))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Get-User-Login-UserId", userId))
                .andExpect(content().json(objectMapper.writeValueAsString(user1)));
        
        verify(authService).login(userId, password);

        when(authService.login(userId, password)).thenReturn(null);

        mockMvc.perform(get("/api/auth/login/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(password))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(new User())));

    }

    @Test
    public void testGetFriendApplicationSet() throws Exception {
        String userId = user1.getUserId();
        Set<String> friendApplications = new HashSet<>(Set.of(user2.getUserId()));
        when(authService.getFriendApplications(userId)).thenReturn(friendApplications);

        mockMvc.perform(get("/api/auth/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Get-FriendApplications-UserId", userId))
                .andExpect(content().json(objectMapper.writeValueAsString(friendApplications)));
        
        verify(authService).getFriendApplications(userId);
    }

    @Test
    public void testNewFriendApplication() throws Exception {
        String response = "Friend application sent.";
        String userId = user1.getUserId(), friendId = user2.getUserId();
        when(authService.newFriendApplication(userId, friendId)).thenReturn(response);

        mockMvc.perform(post("/api/auth/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(friendId))
                .andExpect(status().isAccepted())
                .andExpect(header().string("X-Post-Friend-FriendId", friendId))
                .andExpect(content().string(response));
        
        verify(authService).newFriendApplication(userId, friendId);
        verify(messagingTemplate).convertAndSend("/topic/friend/" + friendId, userId);
    }

    @Test
    public void testDeleteFriend() throws Exception {
        String userId = user1.getUserId(), friendId = user2.getUserId();

        mockMvc.perform(delete("/api/auth/{userId}/{friendId}", userId, friendId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Delete-Friend-FriendId", friendId))
                .andExpect(content().string("Friend delete successful."));
        
        verify(authService).deleteFriend(userId, friendId);
    }

    @Test
    public void testChangePassword() throws Exception {
        String userId = user1.getUserId(), password = user1.getPassword(), newPassword = "23524234";

        when(authService.changePassword(userId, password, newPassword)).thenReturn(true);
        mockMvc.perform(put("/api/auth/password/{userId}", userId)
                .param("password", password)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newPassword))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Put-Password-UserId", userId))
                .andExpect(content().string("Password changed."));
        
        verify(authService).changePassword(userId, password, newPassword);
    }

    @Test
    public void testUpdateUserSettings() {}

    @Test
    public void testGetPublicKey() throws Exception {
        String userId = user1.getUserId();

        when(authService.getPublicKey(userId)).thenReturn(user1.getPublicKey());
        mockMvc.perform(get("/api/auth/publickey/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Get-PublicKey-UserId", userId))
                .andExpect(content().string(user1.getPublicKey()));
        
        verify(authService).getPublicKey(userId);
    }

    @Test
    public void testGetChatRoomIdSet() throws Exception {
        String userId = user1.getUserId();
        Set<String> chatRoomIdSet = new HashSet<>(Set.of("234", "2352"));

        when(authService.getChatRoomIdSet(userId)).thenReturn(chatRoomIdSet);
        mockMvc.perform(get("/api/auth/chatrooms/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Get-ChatRoomIdSet-UserId", userId))
                .andExpect(content().json(objectMapper.writeValueAsString(chatRoomIdSet)));
        
        verify(authService).getChatRoomIdSet(userId);
    }
}
