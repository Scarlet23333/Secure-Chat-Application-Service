package com.example.chatserver.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.chatserver.models.ChatRoom;
import com.example.chatserver.models.User;
import com.example.chatserver.repositories.ChatRoomRepository;
import com.example.chatserver.repositories.MessageRepository;
import com.example.chatserver.repositories.UserRepository;
import com.example.chatserver.utils.RSAUtil;

public class ChatRoomServiceTest {
    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private String publicKey;

    @BeforeEach
    void setup() throws Exception {
        publicKey = RSAUtil.publicKeyToString(RSAUtil.generateKeyPair().getPublic());
    }

    @Test
    public void testCreateChatRoom() {
        String chatRoomId = "1";
        ChatRoom chatRoom = new ChatRoom(chatRoomId, false, new ArrayList<>(List.of("123", "23")), "name");
        User user1 = new User("123", "name1", "pawd", publicKey, "");
        User user2 = new User("23", "name2", "pawd", publicKey, "");
        MockitoAnnotations.openMocks(this);

        when(userRepository.findByUserId("123")).thenReturn(user1);
        when(userRepository.findByUserId("23")).thenReturn(user2);
        when(userRepository.existsById("123")).thenReturn(true);
        when(userRepository.existsById("23")).thenReturn(true);
        when(chatRoomRepository.save(chatRoom)).thenReturn(chatRoom);

        chatRoomService.createChatRoom(chatRoom);

        verify(chatRoomRepository).save(chatRoom);
        verify(userRepository).save(user1);
        verify(userRepository).save(user2);
        verify(messagingTemplate).convertAndSend("/topic/chatroom/" + "123", "Chat room updated.");
        verify(messagingTemplate).convertAndSend("/topic/chatroom/" + "23", "Chat room updated.");
        assertTrue(user1.getChatRoomIdSet().contains(chatRoomId));
        assertTrue(user2.getChatRoomIdSet().contains(chatRoomId));
    }

    @Test
    public void testGetChatRoom() {}
    
    @Test
    public void testAddChatRoomMember() {
        String chatRoomId = "111", userId = "1";
        ChatRoom chatRoom = new ChatRoom(chatRoomId, true, new ArrayList<>(List.of("3", "2")), "room");
        User user = new User("1", "name1", "pawd", publicKey, "");
        MockitoAnnotations.openMocks(this);

        when(chatRoomRepository.existsById(chatRoomId)).thenReturn(true);
        when(chatRoomRepository.findByChatRoomId(chatRoomId)).thenReturn(chatRoom);
        when(userRepository.findByUserId(userId)).thenReturn(user);
        when(userRepository.existsById(userId)).thenReturn(true);

        chatRoomService.addChatRoomMember(chatRoomId, userId);

        verify(chatRoomRepository).save(chatRoom);
        verify(userRepository).save(user);
        verify(messagingTemplate).convertAndSend("/topic/chatroom/" + userId, "Chat room updated.");
        assertTrue(chatRoom.getMemberIdList().contains(userId));
        assertTrue(user.getChatRoomIdSet().contains(chatRoomId));
    }

    @Test
    public void testDeleteChatRoom() {
        String chatRoomId = "1", senderId = "123";
        ChatRoom chatRoom = new ChatRoom(chatRoomId, true, new ArrayList<>(List.of("123", "23")), "name");
        User user1 = new User("123", "name1", "pawd", publicKey, "", new HashSet<>(), new HashSet<>(Set.of(chatRoomId)), new HashSet<>());
        User user2 = new User("23", "name2", "pawd", publicKey, "", new HashSet<>(), new HashSet<>(Set.of(chatRoomId)), new HashSet<>());
        MockitoAnnotations.openMocks(this);

        when(chatRoomRepository.existsById(chatRoomId)).thenReturn(true);
        when(chatRoomRepository.findByChatRoomId(chatRoomId)).thenReturn(chatRoom);
        when(userRepository.findByUserId("123")).thenReturn(user1);
        when(userRepository.findByUserId("23")).thenReturn(user2);
        when(userRepository.existsById("123")).thenReturn(true);
        when(userRepository.existsById("23")).thenReturn(true);

        boolean deleted = chatRoomService.deleteChatRoom(chatRoomId, senderId);

        verify(chatRoomRepository).deleteById(chatRoomId);
        verify(messageRepository).deleteByChatRoomId(chatRoomId);
        verify(userRepository).save(user1);
        verify(userRepository).save(user2);
        verify(messagingTemplate).convertAndSend("/topic/chatroom/" + "123", "Chat room updated.");
        verify(messagingTemplate).convertAndSend("/topic/chatroom/" + "23", "Chat room updated.");
        assertTrue(deleted);
        assertTrue(user1.getChatRoomIdSet().isEmpty());
        assertTrue(user2.getChatRoomIdSet().isEmpty());
    }

    @Test
    public void testDeleteChatRoomMember() {
        String chatRoomId = "1", userId = "23", senderId = "123";
        ChatRoom chatRoom = new ChatRoom(chatRoomId, true, new ArrayList<>(List.of("123", "23")), "name");
        User user = new User(userId, "name1", "pawd", publicKey, "", new HashSet<>(), new HashSet<>(Set.of(chatRoomId)), new HashSet<>());
        MockitoAnnotations.openMocks(this);

        when(chatRoomRepository.findByChatRoomId(chatRoomId)).thenReturn(chatRoom);
        when(userRepository.findByUserId(userId)).thenReturn(user);
        when(userRepository.existsById(userId)).thenReturn(true);

        boolean deleted = chatRoomService.deleteChatRoomMember(chatRoomId, userId, senderId);

        verify(chatRoomRepository).save(chatRoom);
        verify(userRepository).save(user);
        verify(messagingTemplate).convertAndSend("/topic/chatroom/" + userId, "Chat room updated.");
        assertTrue(deleted);
        assertTrue(!chatRoom.getMemberIdList().contains(userId));
        assertTrue(user.getChatRoomIdSet().isEmpty());
    }

}
