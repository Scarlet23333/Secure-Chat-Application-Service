package com.example.chatserver.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/chatrooms")
public class ChatRoomController {

    @GetMapping
    public ResponseEntity<List<ChatRoom>> listChatRooms() {
        // Logic to list all chat rooms
        return ResponseEntity.ok(List.of(new ChatRoom("Room1", List.of("User1", "User2"))));
    }

    @PostMapping
    public ResponseEntity<String> createChatRoom(@RequestBody ChatRoom chatRoom) {
        // Logic to create a new chat room
        return ResponseEntity.ok("Chat room created.");
    }

    @PutMapping("/members")
    public ResponseEntity<String> updateChatRoomMembers(@RequestBody ChatRoomMembersUpdateRequest request) {
        // Logic to update chat room members
        return ResponseEntity.ok("Chat room members updated.");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteChatRoom(@RequestParam String chatRoomId) {
        // Logic to delete a group chat room
        return ResponseEntity.ok("Chat room deleted.");
    }

    @DeleteMapping("/members")
    public ResponseEntity<String> deleteChatRoomMembers(@RequestBody ChatRoomMembersDeleteRequest request) {
        // Logic to remove specific members from a group chat room
        return ResponseEntity.ok("Chat room members removed.");
    }
}
