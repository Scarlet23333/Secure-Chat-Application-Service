package com.example.chatserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor                // For frameworks like Spring to create empty objects
@AllArgsConstructor               // Optional if you still want a constructor with all args
@Document   // (collection = "Message")  // Collection name in MongoDB // Marks this as a MongoDB document
public class Message {
    @Id
    private String chatRoomId;
    
    private String senderId;
    private String content; // encrypted message
    private LocalDateTime timestamp;
}
