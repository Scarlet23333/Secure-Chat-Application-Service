package com.example.chatserver.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.example.chatserver.models.User;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/user")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        // Logic to register user
        // "User registered successfully."
        return ResponseEntity.ok(new String());
    }

    @GetMapping("/login/{userId}")
    public ResponseEntity<String> login(@PathVariable String userId, @RequestBody String password) {
        // Logic to authenticate user and return JWT
        return ResponseEntity.ok(new String());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<String> getFriendApplications(@PathVariable String userId) {
        return ResponseEntity.ok(new String());
    }

    @PostMapping("/{userId}")
    public ResponseEntity<String> handleFriendApplication(@PathVariable String userId, @RequestBody String friendId) {
        // Logic to send/save friend application or add friends
        // "Friend application processed.""
        return ResponseEntity.ok(new String());
    }

    @DeleteMapping("/{userId}/{friendId}")
    public ResponseEntity<String> deleteFriend(@PathVariable String userId, @PathVariable String friendId) {
        // Logic to delete a friend and related one-on-one chat rooms
        return ResponseEntity.ok(new String());
    }

    @PutMapping("/password/{userId}")
    public ResponseEntity<String> changePassword(@PathVariable String userId, @RequestBody String password) {
        // Logic to update the password
        return ResponseEntity.ok(new String());
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<String> updateUserSettings(@PathVariable String userId, @RequestBody boolean disableNotification) {
        // Logic to update user settings
        return ResponseEntity.ok(new String());
    }

    @GetMapping("/publickey/{userId}")
    public ResponseEntity<String> getPublicKey(@PathVariable String userId) {
        // Logic to get pulic key
        return ResponseEntity.ok(new String());
    }

    @GetMapping("/chatrooms/{userId}")
    public ResponseEntity<String> getChatRoomList(@PathVariable String userId) {
        // Logic to get chat room list
        return ResponseEntity.ok(new String());
    }
}
