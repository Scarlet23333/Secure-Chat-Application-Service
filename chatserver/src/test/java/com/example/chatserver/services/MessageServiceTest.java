package com.example.chatserver.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.chatserver.models.ChatRoom;
import com.example.chatserver.models.Message;
import com.example.chatserver.repositories.ChatRoomRepository;
import com.example.chatserver.repositories.MessageRepository;
import com.example.chatserver.utils.TimeConvertUtil;

public class MessageServiceTest {
    @InjectMocks
    private MessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Test
    public void testGetMessages() {}

    @Test
    public void testSaveMessage() {
        // Create a new message
        Message message = new Message("1", "123", "hello", TimeConvertUtil.localDateTimeToTimestamp(LocalDateTime.now()));
        ChatRoom chatRoom = new ChatRoom("1", false, new ArrayList<>(List.of("123", "23")), "name");
        MockitoAnnotations.openMocks(this);

        when(chatRoomRepository.existsById(message.getChatRoomId())).thenReturn(true);
        when(chatRoomRepository.findByChatRoomId(message.getChatRoomId())).thenReturn(chatRoom);
        // Save it to MongoDB
        boolean saved = messageService.saveMessage(message);

        // verify the save is invoked
        verify(messageRepository).save(message);
        // Assert the message was saved
        assertTrue(saved);
    }

    @Test
    public void testDeleteMessage() {}
}
