package com.example.chatserver;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.chatserver.controllers.MessageService;
import com.example.chatserver.models.Message;
import com.example.chatserver.models.MessageRepository;

public class MessageServiceTest {
    @Autowired
    private MessageService messageService;

    @MockBean
    private MessageRepository messageRepository;

    @Test
    public void testSaveMessage() {
        // Create a new message
        Message message = new Message("1", "123", "hello", LocalDateTime.now());
        
        // Save it to MongoDB
        boolean saved = messageService.sendMessage(message);
        Message savedMessage = messageRepository.findByChatRoomIdAndTimeStamp(message.getChatRoomId(), message.getTimestamp());

        // Assert the message was saved
        assertTrue(saved); // Mongo generates an ID for saved documents
        assertEquals("123", savedMessage.getSenderId());
    }
}
