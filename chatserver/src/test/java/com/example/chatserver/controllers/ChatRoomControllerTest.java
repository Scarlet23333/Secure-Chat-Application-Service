package com.example.chatserver.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.chatserver.models.ChatRoom;
import com.example.chatserver.services.ChatRoomService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ChatRoomController.class)
public class ChatRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatRoomService chatRoomService;
    
    @InjectMocks
    private ChatRoomController chatRoomController;

    @Autowired
    private ObjectMapper objectMapper; // To convert object to JSON

    private ChatRoom chatRoom;
    private String chatRoomId;

    @BeforeEach
    void setup() {
        // Setup sample data for testing
        chatRoom = new ChatRoom("23", false, new ArrayList<>(), "one");
        chatRoomId = chatRoom.getChatRoomId();
    }

    @Test
    public void testPostChatRoom() throws Exception {
        mockMvc.perform(post("/api/chatrooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRoom)))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Post-ChatRoom-ChatRoomId", chatRoomId))
                .andExpect(content().string("Chat room created."));
        
        verify(chatRoomService).createChatRoom(chatRoom);
    }
    
    @Test
    public void testGetChatRoom() throws Exception {
        when(chatRoomService.getChatRoom(chatRoomId)).thenReturn(chatRoom);
        mockMvc.perform(get("/api/chatrooms/{chatRoomId}", chatRoomId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Get-Chatroom-ChatRoomId", chatRoomId))
                .andExpect(content().json(objectMapper.writeValueAsString(chatRoom)));
        
        verify(chatRoomService).getChatRoom(chatRoomId);
    }

    @Test
    public void testPutChatRoomMember() throws Exception {
        String userId = "1";
        mockMvc.perform(put("/api/chatrooms/{chatRoomId}", chatRoomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userId))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Put-ChatRoomMember-UserId", userId))
                .andExpect(content().string("Chat room members updated."));
        
        verify(chatRoomService).addChatRoomMember(chatRoomId, userId);
    }

    @Test
    public void testDeleteChatRoom() throws Exception {
        String senderId = "1";
        when(chatRoomService.deleteChatRoom(chatRoomId, senderId)).thenReturn(true);
        mockMvc.perform(delete("/api/chatrooms/{chatRoomId}", chatRoomId)
                .param("senderId", senderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Delete-ChatRoom-ChatRoomId", chatRoomId))
                .andExpect(content().string("Chat room deleted."));
        
        verify(chatRoomService).deleteChatRoom(chatRoomId, senderId);

        when(chatRoomService.deleteChatRoom(chatRoomId, senderId)).thenReturn(false);
        mockMvc.perform(delete("/api/chatrooms/{chatRoomId}", chatRoomId)
                .param("senderId", senderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient permission to delete group chat room."));
    }

    @Test
    public void testDeleteChatRoomMember() throws Exception {
        String userId = "56", senderId = "1";
        when(chatRoomService.deleteChatRoomMember(chatRoomId, userId, senderId)).thenReturn(true);
        mockMvc.perform(delete("/api/chatrooms/{chatRoomId}/{userId}", chatRoomId, userId)
                .param("senderId", senderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Delete-ChatRoomMember-UserId", userId))
                .andExpect(content().string("Chat room member removed."));
        
        verify(chatRoomService).deleteChatRoomMember(chatRoomId, userId, senderId);

        when(chatRoomService.deleteChatRoomMember(chatRoomId, userId, senderId)).thenReturn(false);
        mockMvc.perform(delete("/api/chatrooms/{chatRoomId}/{userId}", chatRoomId, userId)
                .param("senderId", senderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient permission to remove chat room member."));
    }
}
