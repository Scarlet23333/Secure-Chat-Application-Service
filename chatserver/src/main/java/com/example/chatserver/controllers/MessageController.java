package com.example.chatserver.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.chatserver.models.Message;
import com.example.chatserver.services.MessageService;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<List<Message>> getMessages(@PathVariable("chatRoomId") String chatRoomId) {
        // Logic to fetch message history for a chat room
        List<Message> history = messageService.getMessages(chatRoomId);
        return ResponseEntity.ok().header("X-Get-History-chatRoomId", chatRoomId).body(history);
    }

    @PostMapping("/{chatRoomId}")
    public ResponseEntity<String> sendMessage(@PathVariable("chatRoomId") String chatRoomId, @RequestBody Message message) {
        // Logic to send a message to the chat room
        if (!messageService.saveMessage(message))
            return ResponseEntity.badRequest().body("Message send failed.");
        // send a message update notice to the chat room
        messagingTemplate.convertAndSend("/topic/messages/" + chatRoomId, "message update");
        return ResponseEntity.ok().header("X-Post-Message-chatRoomId", chatRoomId).body("Message sent.");
    }

    @DeleteMapping("/{chatRoomId}")
    public ResponseEntity<String> deleteMessage(@PathVariable("chatRoomId") String chatRoomId, @RequestParam("timestamp") long timestamp) {
        // Logic to delete user's own message
        messageService.deleteMessage(chatRoomId, timestamp);
        // send a message update notice to the chat room
        messagingTemplate.convertAndSend("/topic/messages/" + chatRoomId, "message update");
        return ResponseEntity.ok().header("X-Delete-Message-chatRoomId", chatRoomId).body("Message deleted.");
    }
}
