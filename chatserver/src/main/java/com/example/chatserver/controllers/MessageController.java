package com.example.chatserver.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.example.chatserver.models.Message;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<String> getMessages(@PathVariable String chatRoomID) {
        // Logic to fetch message history for a chat room
        return ResponseEntity.ok(new String());
    }

    @PostMapping("/{chatRoomId}")
    public ResponseEntity<String> sendMessage(@PathVariable String chatRoomID, @RequestBody Message message) {
        // Logic to send a message to the chat room
        return ResponseEntity.ok("Message sent.");
    }

    @DeleteMapping("/{chatRoomId}/{timeStamp}")
    public ResponseEntity<String> deleteMessage(@PathVariable String chatRoomID, @PathVariable String timeStamp) {
        // Logic to delete user's own messages
        return ResponseEntity.ok("Message deleted.");
    }
}
