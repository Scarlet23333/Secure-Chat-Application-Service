package com.example.chatserver.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.chatserver.models.ChatRoomRepository;
import com.example.chatserver.models.Message;
import com.example.chatserver.models.MessageRepository;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public List<Message> getMessages(String chatRoomId) {
        return messageRepository.findByChatRoomId(chatRoomId);
    }

    @Transactional
    public boolean sendMessage(Message message) {
        if (!chatRoomRepository.existsById(message.getChatRoomId()))
            return false;
        messageRepository.save(message);
        // TODO: Logic to send a message to the chat room
        return true;
    }

    public void deleteMessage(String charRoomId, LocalDateTime timeStamp) {
        messageRepository.deleteByChatRoomIdAndTimeStamp(charRoomId, timeStamp);
    }
}
