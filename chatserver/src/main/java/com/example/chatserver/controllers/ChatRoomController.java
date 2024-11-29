package com.example.chatserver.controllers;

import org.springframework.web.bind.annotation.*;

import com.example.chatserver.models.ChatRoom;
import com.example.chatserver.services.ChatRoomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/chatrooms")
public class ChatRoomController {
    @Autowired
    private ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<String> postChatRoom(@RequestBody ChatRoom chatRoom) {
        // Logic to create or update a chat room
        return ResponseEntity.ok().header("X-Post-ChatRoom-ChatRoomId", chatRoom.getChatRoomId()).body(chatRoomService.createChatRoom(chatRoom));
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatRoom> getChatRoom(@PathVariable("chatRoomId") String chatRoomId) {
        // Logic to update chat room members
        return ResponseEntity.ok().header("X-Get-Chatroom-ChatRoomId", chatRoomId).body(chatRoomService.getChatRoom(chatRoomId));
    }

    @PutMapping("/{chatRoomId}")
    public ResponseEntity<String> putChatRoomMember(@PathVariable("chatRoomId") String chatRoomId, @RequestBody String userId) {
        // Logic to update chat room members
        chatRoomService.addChatRoomMember(chatRoomId, userId);
        return ResponseEntity.ok().header("X-Put-ChatRoomMember-UserId", userId).body("Chat room members updated.");
    }

    @DeleteMapping("/{chatRoomId}")
    public ResponseEntity<String> deleteChatRoom(@PathVariable("chatRoomId") String chatRoomId, @RequestParam("senderId") String senderId) {
        // Logic to delete a group chat room
        if (chatRoomService.deleteChatRoom(chatRoomId, senderId))
            return ResponseEntity.ok().header("X-Delete-ChatRoom-ChatRoomId", chatRoomId).body("Chat room deleted.");
        else
            return ResponseEntity.badRequest().body("Insufficient permission to delete group chat room.");
    }

    @DeleteMapping("/{chatRoomId}/{userId}")
    public ResponseEntity<String> deleteChatRoomMember(@PathVariable("chatRoomId") String chatRoomId, @PathVariable("userId") String userId, @RequestParam("senderId") String senderId) {
        // Logic to remove a specific member from a group chat room
        if (chatRoomService.deleteChatRoomMember(chatRoomId, userId, senderId))
            return ResponseEntity.ok().header("X-Delete-ChatRoomMember-UserId", userId).body("Chat room member removed.");
        else
            return ResponseEntity.badRequest().body("Insufficient permission to remove chat room member.");
    }
}
