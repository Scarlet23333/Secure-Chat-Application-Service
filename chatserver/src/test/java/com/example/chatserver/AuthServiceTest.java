package com.example.chatserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.chatserver.controllers.AuthService;
import com.example.chatserver.controllers.ChatRoomService;
import com.example.chatserver.models.ChatRoom;
import com.example.chatserver.models.ChatRoomRepository;
import com.example.chatserver.models.User;
import com.example.chatserver.models.UserRepository;

// @SpringBootTest
public class AuthServiceTest {

    @Mock
    private ChatRoomService chatRoomService;
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    public void testCreateUser() {}

    @Test
    public void testLogin() {}

    @Test
    public void testGetFriendApplications() {}

    @Test
    public void testNewFriendApplication() {

    }

    @Test
    public void testDeleteFriend() {
        String userId = "1", friendId = "2", chatRoomId1 = "98", chatRoomId2 = "58";
        MockitoAnnotations.openMocks(this);

        Set<String> friendIdSet1 = new HashSet<>(Set.of("21", friendId)), friendIdSet2 = new HashSet<>(Set.of("21"));
        Set<String> chatRoomIdSet = new HashSet<>(Set.of(chatRoomId1, chatRoomId2));
        User user1 = new User(userId, "name", "1233342", "23453425", "", friendIdSet1, chatRoomIdSet, null);
        User user2 = new User(userId, "name", "1233342", "23453425", "", friendIdSet2, chatRoomIdSet, null);
        when(userRepository.findByUserId(userId)).thenReturn(user1);
        when(userRepository.save(notNull())).thenReturn(user2);
        List<String> memberlList1 = List.of(userId, friendId), memberlList2 = List.of(userId, friendId, "21");
        ChatRoom chatRoom1 = new ChatRoom(false, memberlList1, "chatRoom1");
        chatRoom1.setChatRoomId(chatRoomId1);
        when(chatRoomRepository.findByChatRoomId("98")).thenReturn(chatRoom1);
        ChatRoom chatRoom2 = new ChatRoom(true, memberlList2, "chatRoom2");
        chatRoom2.setChatRoomId(chatRoomId2);
        when(chatRoomRepository.findByChatRoomId("58")).thenReturn(chatRoom2);
        // delete chat room of user should be test in ChatRoomServiceTest
        when(chatRoomService.deleteChatRoom(notNull(), eq(userId))).thenReturn(true);
        User modifiedUser = authService.deleteFriend(userId, friendId);

        // Assert the the friend is deleted
        assertEquals(user2, modifiedUser);
    }

    @Test
    public void testChangePassword() {}

    @Test
    public void testGetPublicKey() {}

    @Test
    public void testGetChatRoomIdSet() {}
}
