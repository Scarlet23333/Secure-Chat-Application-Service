package com.example.chatserver.models;

import java.util.List;

import lombok.Data;
import lombok.NonNull;

@Data
public class User {
    private final String userId;
    @NonNull private String username;
    @NonNull private String password; // encrypted password
    @NonNull private String publicKey;
    private List<String> friendIdList; // friend user id list
    private String profile;
    private List<String> chatRoomIdList;
    private boolean disableNotification;

    public User(String userId) {
        this.userId = userId;
        disableNotification = false;
    }
}
