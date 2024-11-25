package com.example.chatserver.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import com.example.chatserver.models.User;
import com.example.chatserver.utils.RSAUtil;

@DataMongoTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;
    private String userId;

    @BeforeEach
    void setup() throws Exception {
        String publicKey = RSAUtil.publicKeyToString(RSAUtil.generateKeyPair().getPublic());
        userId = "12";
        user = new User(userId, "username", "pswd", publicKey, "");
    }

    @Test
    void testSaveChatRoom() {
        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUserId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("username");
    }

    @Test
    void testFindByUserId() {
        // Arrange
        userRepository.save(user);

        // Act
        User foundUser = userRepository.findByUserId(userId);

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("username");
    }

    @Test
    void testExistsById() {
        // Arrange
        userRepository.save(user);

        // Act
        boolean exists = userRepository.existsById(userId);

        // Assert
        assertThat(exists).isTrue();
    }
}
