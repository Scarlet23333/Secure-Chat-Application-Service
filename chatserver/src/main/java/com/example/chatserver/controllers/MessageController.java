package com.example.chatserver.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.example.chatserver.models.Message;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<List<Message>> getMessages(@PathVariable String chatRoomID) {
        // Logic to fetch message history for a chat room
        List<Message> history = messageService.getMessages(chatRoomID);
        return ResponseEntity.ok().header("X-Get-History-ChatRoomId", chatRoomID).body(history);
    }

    @PostMapping("/{chatRoomId}")
    public ResponseEntity<String> sendMessage(@PathVariable String chatRoomID, @RequestBody Message message) {
        // Logic to send a message to the chat room
        if (messageService.sendMessage(message))
            return ResponseEntity.ok().header("X-Post-Message-ChatRoomId", chatRoomID).body("Message sent.");
        else
            return ResponseEntity.badRequest().body("Message send failed.");
    }

    @DeleteMapping("/{chatRoomId}/{timeStamp}")
    public ResponseEntity<String> deleteMessage(@PathVariable String chatRoomID, @PathVariable LocalDateTime timeStamp) {
        // Logic to delete user's own message
        messageService.deleteMessage(chatRoomID, timeStamp);
        return ResponseEntity.ok().header("X-Delete-Message-ChatRoomId", chatRoomID).body("Message deleted.");
    }
}
