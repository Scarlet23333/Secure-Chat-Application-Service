package com.example.chatserver.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Sort;

import com.example.chatserver.models.Message;
import com.example.chatserver.utils.TimeConvertUtil;

@DataMongoTest
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    private Message message1, message2;
    private String chatRoomId;

    @BeforeEach
    void setup() {
        chatRoomId = "123";
        message1 = new Message(chatRoomId, "3", "hello", TimeConvertUtil.localDateTimeToTimestamp(LocalDateTime.now()));
        message2 = new Message(chatRoomId, "1", "hi", TimeConvertUtil.localDateTimeToTimestamp(LocalDateTime.now()));
    }

    @Test
    void testsaveMessage() {
        // Act
        Message savedMessage = messageRepository.save(message1);

        // Assert
        assertThat(savedMessage).isNotNull();
        assertThat(savedMessage.getChatRoomId()).isNotNull();
        assertThat(savedMessage.getContent()).isEqualTo("hello");
    }

    @Test
    void testFindByChatRoomId() {
        // Arrange
        messageRepository.save(message2);
        Sort sort = Sort.by(Sort.Direction.ASC, "timestamp");

        // Act
        List<Message> foundMessages = messageRepository.findByChatRoomId(chatRoomId, sort);
        int messageNumber = foundMessages.size();
        // Assert
        assertThat(foundMessages).isNotNull();
        assertThat(messageNumber).isGreaterThan(1);
        assertThat(foundMessages.get(messageNumber - 1)).isEqualTo(message2);
    }

    @Test
    void testDeleteOrFindByChatRoomIdAndTimestamp() {
        // Arrange
        messageRepository.save(message2);

        // Act
        messageRepository.deleteByChatRoomIdAndTimestamp(chatRoomId, message2.getTimestamp());
        Message deletedMessage = messageRepository.findByChatRoomIdAndTimestamp(chatRoomId, message2.getTimestamp());

        // Assert
        assertThat(deletedMessage).isNull();
    }

    @Test
    void testDeleteByChatRoomId() {
        // Arrange
        messageRepository.save(message1);
        messageRepository.save(message2);

        // Act
        messageRepository.deleteByChatRoomId(chatRoomId);
        Message deletedMessage = messageRepository.findById(chatRoomId).orElse(null);

        // Assert
        assertThat(deletedMessage).isNull();
    }
}
