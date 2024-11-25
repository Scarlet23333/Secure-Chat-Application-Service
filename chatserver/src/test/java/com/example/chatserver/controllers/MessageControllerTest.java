package com.example.chatserver.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.chatserver.models.Message;
import com.example.chatserver.services.MessageService;
import com.example.chatserver.utils.TimeConvertUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @MockitoBean
    private SimpMessagingTemplate messagingTemplate;
    
    @InjectMocks
    private MessageController messageController;

    @Autowired
    private ObjectMapper objectMapper; // To convert object to JSON

    private String chatRoomId;
    private Message message1, message2;

    @BeforeEach
    void setup() {
        // Setup sample data for testing
        chatRoomId = "123";
        message1 = new Message(chatRoomId, "1", "hello", TimeConvertUtil.localDateTimeToTimestamp(LocalDateTime.now()));
        message2 = new Message(chatRoomId, "2", "hi", TimeConvertUtil.localDateTimeToTimestamp(LocalDateTime.now()));
    }

    @Test
    public void testGetMessage() throws Exception {
        when(messageService.getMessages(chatRoomId)).thenReturn(List.of(message1, message2));
        
        String expectedJson = objectMapper.writeValueAsString(List.of(message1, message2));
        // Act and Assert
        mockMvc.perform(get("/api/messages/{chatRoomId}", chatRoomId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Get-History-chatRoomId", chatRoomId))
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void testSendMessage() throws Exception {
        when(messageService.saveMessage(message1)).thenReturn(true);
        mockMvc.perform(post("/api/messages/{chatRoomId}", chatRoomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message1)))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Post-Message-chatRoomId", chatRoomId))
                .andExpect(content().string("Message sent."));

        verify(messagingTemplate).convertAndSend("/topic/messages/" + chatRoomId, message1);
        
        when(messageService.saveMessage(message1)).thenReturn(false);
        mockMvc.perform(post("/api/messages/{chatRoomId}", chatRoomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Message send failed."));
    }

    @Test
    public void testDeleteMessage() throws Exception {
        mockMvc.perform(delete("/api/messages/{chatRoomId}", chatRoomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message1.getTimestamp())))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Delete-Message-chatRoomId", chatRoomId))
                .andExpect(content().string("Message deleted."));

        verify(messageService).deleteMessage(chatRoomId, message1.getTimestamp());
    }
}
