package com.example.chatserver.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.chatserver.models.ChatRoom;
import com.example.chatserver.models.User;
import com.example.chatserver.repositories.ChatRoomRepository;
import com.example.chatserver.repositories.UserRepository;
import com.example.chatserver.utils.RSAUtil;

public class AuthServiceTest {

    @Mock
    private ChatRoomService chatRoomService;
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @InjectMocks
    private AuthService authService;

    private String publicKey;

    @BeforeEach
    void setup() throws Exception {
        publicKey = RSAUtil.publicKeyToString(RSAUtil.generateKeyPair().getPublic());
    }

    @Test
    public void testCreateUser() {}

    @Test
    public void testLogin() {}

    @Test
    public void testGetFriendApplications() {}

    @Test
    public void testNewFriendApplication() {
        String userId = "1", friendId = "2";
        Set<String> friendApplicationSenderIdSet1 = new HashSet<>(Set.of(friendId, "234"));
        Set<String> friendApplicationSenderIdSet2 = new HashSet<>(Set.of("45"));
        User user = new User(userId, "name", "pswd", publicKey, "", 
                    new HashSet<>(), new HashSet<>(), friendApplicationSenderIdSet1);
        User friend = new User(friendId, "frd", "pswd", publicKey, "",
                    new HashSet<>(), new HashSet<>(), friendApplicationSenderIdSet2);
        MockitoAnnotations.openMocks(this);

        when(userRepository.existsById(notNull())).thenReturn(true);
        when(userRepository.findByUserId(userId)).thenReturn(user);
        when(userRepository.findByUserId(friendId)).thenReturn(friend);

        String acceptedString = authService.newFriendApplication(userId, friendId);
        String existString = authService.newFriendApplication(userId, friendId);
        user.setFriendIdSet(new HashSet<>());
        String sentString = authService.newFriendApplication(userId, friendId);

        assertEquals("Friend application accepted.", acceptedString);
        assertEquals("Friend already exists.", existString);
        assertEquals("Friend application sent.", sentString);
        assertTrue(friend.getFriendApplicationSenderIdSet().contains(userId));
        verify(userRepository).save(user);
        verify(userRepository).save(friend);
    }

    @Test
    public void testDeleteFriend() {
        String userId = "1", friendId = "2", chatRoomId1 = "98", chatRoomId2 = "58";
        Set<String> friendIdSet = new HashSet<>(Set.of("21", friendId));
        Set<String> chatRoomIdSet = new HashSet<>(Set.of(chatRoomId1, chatRoomId2));
        User user = new User(userId, "name", "1233342", publicKey, "", friendIdSet, chatRoomIdSet, null);
        List<String> memberlList1 = List.of(userId, friendId), memberlList2 = List.of(userId, friendId, "21");
        ChatRoom chatRoom1 = new ChatRoom(chatRoomId1, false, memberlList1, "chatRoom1");
        ChatRoom chatRoom2 = new ChatRoom(chatRoomId2, true, memberlList2, "chatRoom2");
        
        MockitoAnnotations.openMocks(this);

        when(userRepository.findByUserId(userId)).thenReturn(user);
        when(chatRoomRepository.findByChatRoomId("98")).thenReturn(chatRoom1);
        when(chatRoomRepository.findByChatRoomId("58")).thenReturn(chatRoom2);
        
        User modifiedUser = authService.deleteFriend(userId, friendId);

        // Assert the the friend is deleted
        assertEquals(user, modifiedUser);
        verify(userRepository).save(user);
        verify(chatRoomService).deleteChatRoom(chatRoomId1, userId);
    }

    @Test
    public void testChangePassword() {}

    @Test
    public void testGetPublicKey() {}

    @Test
    public void testGetChatRoomIdSet() {}
}
