package com.example.chatserver.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.chatserver.models.ChatRoom;
import com.example.chatserver.models.User;
import com.example.chatserver.repositories.ChatRoomRepository;
import com.example.chatserver.repositories.MessageRepository;
import com.example.chatserver.repositories.UserRepository;

public class ChatRoomServiceTest {
    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testCreateChatRoom() {
        String chatRoomId = "1";
        ChatRoom chatRoom = new ChatRoom(chatRoomId, false, new ArrayList<>(List.of("123", "23")), "name");
        User user1 = new User("123", "name1", "pawd", "", "");
        User user2 = new User("23", "name2", "pawd", "", "");
        MockitoAnnotations.openMocks(this);

        when(userRepository.findByUserId("123")).thenReturn(user1);
        when(userRepository.findByUserId("23")).thenReturn(user2);

        chatRoomService.createChatRoom(chatRoom);

        verify(chatRoomRepository).save(chatRoom);
        verify(userRepository).save(user1);
        verify(userRepository).save(user2);
        assertTrue(user1.getChatRoomIdSet().contains(chatRoomId));
        assertTrue(user2.getChatRoomIdSet().contains(chatRoomId));
    }

    @Test
    public void testGetChatRoom() {}
    
    @Test
    public void testAddChatRoomMember() {
        String chatRoomId = "111", userId = "1";
        ChatRoom chatRoom = new ChatRoom(chatRoomId, true, new ArrayList<>(List.of("3", "2")), "room");
        User user = new User("1", "name1", "pawd", "", "");
        MockitoAnnotations.openMocks(this);

        when(chatRoomRepository.findByChatRoomId(chatRoomId)).thenReturn(chatRoom);
        when(userRepository.findByUserId(userId)).thenReturn(user);

        chatRoomService.addChatRoomMember(chatRoomId, userId);

        verify(chatRoomRepository).save(chatRoom);
        verify(userRepository).save(user);
        assertTrue(chatRoom.getMemberIdList().contains(userId));
        assertTrue(user.getChatRoomIdSet().contains(chatRoomId));
    }

    @Test
    public void testDeleteChatRoom() {
        String chatRoomId = "1", senderId = "123";
        ChatRoom chatRoom = new ChatRoom(chatRoomId, true, new ArrayList<>(List.of("123", "23")), "name");
        User user1 = new User("123", "name1", "pawd", "", "", new HashSet<>(), new HashSet<>(Set.of(chatRoomId)), new HashSet<>());
        User user2 = new User("23", "name2", "pawd", "", "", new HashSet<>(), new HashSet<>(Set.of(chatRoomId)), new HashSet<>());
        MockitoAnnotations.openMocks(this);

        when(chatRoomRepository.findByChatRoomId(chatRoomId)).thenReturn(chatRoom);
        when(userRepository.findByUserId("123")).thenReturn(user1);
        when(userRepository.findByUserId("23")).thenReturn(user2);

        boolean deleted = chatRoomService.deleteChatRoom(chatRoomId, senderId);

        verify(chatRoomRepository).deleteById(chatRoomId);
        verify(messageRepository).deleteByChatRoomId(chatRoomId);
        verify(userRepository).save(user1);
        verify(userRepository).save(user2);
        assertTrue(deleted);
        assertTrue(user1.getChatRoomIdSet().isEmpty());
        assertTrue(user2.getChatRoomIdSet().isEmpty());
    }

    @Test
    public void testDeleteChatRoomMember() {
        String chatRoomId = "1", userId = "23", senderId = "123";
        ChatRoom chatRoom = new ChatRoom(chatRoomId, true, new ArrayList<>(List.of("123", "23")), "name");
        User user = new User(userId, "name1", "pawd", "", "", new HashSet<>(), new HashSet<>(Set.of(chatRoomId)), new HashSet<>());
        MockitoAnnotations.openMocks(this);

        when(chatRoomRepository.findByChatRoomId(chatRoomId)).thenReturn(chatRoom);
        when(userRepository.findByUserId(userId)).thenReturn(user);

        boolean deleted = chatRoomService.deleteChatRoomMember(chatRoomId, userId, senderId);

        verify(chatRoomRepository).save(chatRoom);
        verify(userRepository).save(user);
        assertTrue(deleted);
        assertTrue(!chatRoom.getMemberIdList().contains(userId));
        assertTrue(user.getChatRoomIdSet().isEmpty());
    }

}
