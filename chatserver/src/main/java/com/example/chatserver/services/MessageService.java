package com.example.chatserver.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
        Sort sort = Sort.by(Sort.Direction.ASC, "timestamp");
        return messageRepository.findByChatRoomId(chatRoomId, sort);
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

    public void deleteMessage(String charRoomId, long timestamp) {
        messageRepository.deleteByChatRoomIdAndTimestamp(charRoomId, timestamp);
    }
}
