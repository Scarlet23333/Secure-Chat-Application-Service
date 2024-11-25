package com.example.chatserver.repositories;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.chatserver.models.Message;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByChatRoomId(String chatRoomId, Sort sort);
    // Message saveMessage(Message message);
    void deleteByChatRoomIdAndTimestamp(String chatRoomId, long timestamp);
    Message findByChatRoomIdAndTimestamp(String chatRoomId, long timestamp);
    void deleteByChatRoomId(String chatRoomId);
}
