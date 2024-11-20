package com.example.chatserver.models;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    // ChatRoom saveChatRoom(ChatRoom chatRoom);
    ChatRoom findByChatRoomId(String chatRoomid);
    // void deleteByChatRoomId(String chatRoomId);
}