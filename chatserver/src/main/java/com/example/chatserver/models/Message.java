package com.example.chatserver.models;

import lombok.Data;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document   // (collection = "Message")  // Collection name in MongoDB
public class Message {
    @Id
    private String chatRoomId;
    
    private String senderId;
    private String content; // encrypted message
    private LocalDateTime timestamp;

    public Message(String chatRoomId, String senderId, String content, LocalDateTime timestamp) {
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
    }
}
