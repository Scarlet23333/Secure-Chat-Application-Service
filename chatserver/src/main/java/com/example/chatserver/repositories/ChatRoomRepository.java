package com.example.chatserver.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.chatserver.models.ChatRoom;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    // ChatRoom saveChatRoom(ChatRoom chatRoom);
    ChatRoom findByChatRoomId(String chatRoomid);
    // void deleteByChatRoomId(String chatRoomId);
}