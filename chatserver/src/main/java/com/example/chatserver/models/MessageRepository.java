package com.example.chatserver.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByChatRoomId(String chatRoomId);
    // Message saveMessage(Message message);
    void deleteByChatRoomIdAndTimeStamp(String chatRoomId, LocalDateTime timeStamp);
    Message findByChatRoomIdAndTimeStamp(String chatRoomId, LocalDateTime timeStamp);
    // void deleteAllByChatRoomId(String chatRoomId);
}
