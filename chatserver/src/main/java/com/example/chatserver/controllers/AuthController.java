package com.example.chatserver.controllers;

import org.springframework.web.bind.annotation.*;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.example.chatserver.models.User;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/user")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        // Logic to register a new user
        if (authService.createUser(user))
            return ResponseEntity.ok().header("X-Create-User-UserId", user.getUserId()).body("User registered.");
        else
            return ResponseEntity.badRequest().body("User id existed.");
    }

    @GetMapping("/login/{userId}")
    public ResponseEntity<User> login(@PathVariable String userId, @RequestBody String password) {
        // Logic to authenticate user // and return JWT
        User user = authService.login(userId, password);
        if (user != null)
            return ResponseEntity.ok().header("X-Get-User-Login-UserId", userId).body(user);
        else
            return ResponseEntity.badRequest().body(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Set<String>> getFriendApplications(@PathVariable String userId) {
        // Logic to get all off line friend applications
        return ResponseEntity.ok().header("X-Get-FriendApplications-UserId", userId).body(authService.getFriendApplications(userId));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<String> newFriendApplication(@PathVariable String userId, @RequestBody String friendId) {
        // Logic to send/save friend application or add friends
        // "Friend application processed.""
        String response = authService.newFriendApplication(userId, friendId);
        if (response.equals("Invalid Friend ID."))
            return ResponseEntity.badRequest().body(response);
        else if (response.equals("Friend application accepted."))
            return ResponseEntity.ok().header("X-Post-Friend-FriendId", friendId).body(response);
        else
            return ResponseEntity.accepted().header("X-Post-Friend-FriendId", friendId).body(response);
    }

    @DeleteMapping("/{userId}/{friendId}")
    public ResponseEntity<String> deleteFriend(@PathVariable String userId, @PathVariable String friendId) {
        // Logic to delete a friend and related one-on-one chat rooms
        authService.deleteFriend(userId, friendId);
        return ResponseEntity.ok().header("X-Delete-Friend-FriendId", friendId).body("Friend delete successful.");
    }

    @PutMapping("/password/{userId}")
    public ResponseEntity<String> changePassword(@PathVariable String userId, @RequestBody String password, @RequestBody String newPassword) {
        // Logic to change the password
        if (authService.changePassword(userId, password, newPassword))
            return ResponseEntity.ok().header("X-Put-Password-UserId", userId).body("Password changed.");
        else
            return ResponseEntity.badRequest().body("Password change failed.");
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<String> updateUserSettings(@PathVariable String userId, @RequestBody boolean disableNotification) {
        // TODO: Logic to update user settings
        return ResponseEntity.ok(new String());
    }

    @GetMapping("/publickey/{userId}")
    public ResponseEntity<String> getPublicKey(@PathVariable String userId) {
        // Logic to get pulic key
        return ResponseEntity.ok().header("X-Get-PublicKey-UserId", userId).body(authService.getPublicKey(userId));
    }

    @GetMapping("/chatrooms/{userId}")
    public ResponseEntity<Set<String>> getChatRoomIdSet(@PathVariable String userId) {
        // Logic to get chat room list
        return ResponseEntity.ok().header("X-Get-ChatRoomIdSet-UserId", userId).body(authService.getChatRoomIdSet(userId));
    }
}
