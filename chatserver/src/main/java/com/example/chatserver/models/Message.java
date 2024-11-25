package com.example.chatserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor                // For frameworks like Spring to create empty objects
@AllArgsConstructor               // Optional if you still want a constructor with all args
@Document   // (collection = "Message")  // Collection name in MongoDB // Marks this as a MongoDB document
@CompoundIndexes({
    @CompoundIndex(name = "unique_chatRoomId_timestamp", def = "{'chatRoomId': 1, 'timestamp': 1}", unique = true)
})
public class Message {
    @Indexed
    private String chatRoomId;
    
    private String senderId;
    private String content; // encrypted message

    @DateTimeFormat
    private long timestamp;
}
