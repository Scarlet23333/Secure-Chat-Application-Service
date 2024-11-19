package com.example.chatserver.models;

import java.util.List;

import lombok.Data;

@Data
public class ChatRoom {
    private final String chatRoomId;
    private final boolean isGroupChatRoom;
    private List<String> memberIdList;
    private String chatRoomName;

    public ChatRoom(String chatRoomId, boolean isGroupChatRoom) {
        this.chatRoomId = chatRoomId;
        this.isGroupChatRoom = isGroupChatRoom;
    }
}
