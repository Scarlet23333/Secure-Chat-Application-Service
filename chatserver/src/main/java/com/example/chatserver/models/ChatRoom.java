package com.example.chatserver.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NonNull;

@Data
@Document   // (collection = "ChatRoom")  // Collection name in MongoDB
public class ChatRoom {
    @Id
    private String chatRoomId;

    private boolean isGroupChatRoom;
    @NonNull private List<String> memberIdList;
    @NonNull private String chatRoomName;

    public ChatRoom(boolean isGroupChatRoom, List<String> memberIdList, String chatRoomName) {
        this.isGroupChatRoom = isGroupChatRoom;
        this.memberIdList = memberIdList;
        this.chatRoomName = chatRoomName;
    }
}
