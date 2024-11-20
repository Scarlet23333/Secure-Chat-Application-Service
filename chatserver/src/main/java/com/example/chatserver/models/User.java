package com.example.chatserver.models;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NonNull;

@Data
@Document   // (collection = "User")  // Collection name in MongoDB
public class User {
    @Id
    @NonNull private String userId;
    
    @NonNull private String username;
    @NonNull private String password; // encrypted password
    @NonNull private String publicKey;
    @NonNull private String profile;

    // optional
    // private boolean disableNotification;
    private Set<String> friendIdSet; // friend user id Set
    private Set<String> chatRoomIdSet;
    private Set<String> friendApplicationSenderIdSet;
    // user session setting in other model

    public User(String userId, String username, String password, String publicKey, String profile) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.publicKey = publicKey;
        this.profile = profile;
        friendIdSet = new HashSet<>();
        chatRoomIdSet = new HashSet<>();
        friendApplicationSenderIdSet = new HashSet<>();
    }

    public User(String userId, String username, String password, String publicKey, String profile, 
    Set<String> friendIdSet, Set<String> chatRoomIdSet, Set<String> friendApplicationSenderIdSet) {
        this(userId, username, password, publicKey, profile);
        this.friendIdSet = friendIdSet;
        this.chatRoomIdSet = chatRoomIdSet;
        this.friendApplicationSenderIdSet = friendApplicationSenderIdSet;
    }
}
