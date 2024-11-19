package com.example.chatserver.models;

import lombok.Data;

@Data
public class Message {
    private final String senderId;
    private final String content; // encrypted message
    private final String timestamp;
    private final String chatRoomId;
}
