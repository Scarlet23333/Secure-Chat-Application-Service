package com.example.chatserver.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.chatserver.models.Message;
import com.example.chatserver.repositories.ChatRoomRepository;
import com.example.chatserver.repositories.MessageRepository;

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
    public boolean saveMessage(Message message) {
        if (!chatRoomRepository.existsById(message.getChatRoomId()))
            return false;
        List<String> memberList = chatRoomRepository.findByChatRoomId(message.getChatRoomId()).getMemberIdList();
        if (!memberList.contains(message.getSenderId()))
            return false;
        messageRepository.save(message);
        return true;
    }

    public void deleteMessage(String charRoomId, LocalDateTime timeStamp) {
        messageRepository.deleteByChatRoomIdAndTimeStamp(charRoomId, timeStamp);
    }
}
