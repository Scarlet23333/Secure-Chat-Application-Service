package com.example.chatserver.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import com.example.chatserver.models.ChatRoom;

@DataMongoTest
public class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    private ChatRoom chatRoom;
    private String chatRoomId;

    @BeforeEach
    void setup() {
        chatRoomId = "123";
        chatRoom = new ChatRoom(chatRoomId, true, List.of("user1", "user2"), "Test Chat Room");
    }

    @Test
    void testSaveChatRoom() {
        // Act
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // Assert
        assertThat(savedChatRoom).isNotNull();
        assertThat(chatRoom.getChatRoomId()).isNotNull();
        assertThat(savedChatRoom.getChatRoomId()).isNotNull();
        assertThat(savedChatRoom.getChatRoomName()).isEqualTo("Test Chat Room");
    }

    @Test
    void testFindByChatRoomId() {
        // Arrange
        chatRoomRepository.save(chatRoom);

        // Act
        ChatRoom foundChatRoom = chatRoomRepository.findByChatRoomId(chatRoomId);

        // Assert
        assertThat(foundChatRoom).isNotNull();
        assertThat(foundChatRoom.getChatRoomName()).isEqualTo("Test Chat Room");
    }

    @Test
    void testDeleteChatRoom() {
        // Arrange
        chatRoomRepository.save(chatRoom);

        // Act
        chatRoomRepository.deleteById(chatRoomId);
        ChatRoom deletedChatRoom = chatRoomRepository.findByChatRoomId(chatRoomId);

        // Assert
        assertThat(deletedChatRoom).isNull();
    }
}
