package com.example.chatserver.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.chatserver.models.ChatRoom;
import com.example.chatserver.models.ChatRoomRepository;
import com.example.chatserver.models.MessageRepository;
import com.example.chatserver.models.User;
import com.example.chatserver.models.UserRepository;

@Service
public class ChatRoomService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    private void updateChatRoomIdSet(ChatRoom chatRoom, boolean isAdd) {
        List<String> memberIdList = chatRoom.getMemberIdList();
        String chatRoomId = chatRoom.getChatRoomId();
        for (String userId: memberIdList) {
            User user = userRepository.findByUserId(userId);
            Set<String> chatRoomIdSet = user.getChatRoomIdSet();
            if (isAdd)
                chatRoomIdSet.add(chatRoomId);
            else
                chatRoomIdSet.remove(chatRoomId);
            user.setChatRoomIdSet(chatRoomIdSet);
            userRepository.save(user);
        }
    }

    @Transactional
    public void createChatRoom(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
        // add chat room id to related user
        updateChatRoomIdSet(chatRoom, true);
    }

    public ChatRoom getChatRoom(String chatRoomId) {
        return chatRoomRepository.findByChatRoomId(chatRoomId);
    }

    public void addChatRoomMember(String chatRoomId, String userId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId);
        List<String> memberIdList = chatRoom.getMemberIdList();
        if (!memberIdList.contains(userId))
            memberIdList.add(userId);
        chatRoom.setMemberIdList(memberIdList);
        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public boolean deleteChatRoom(String chatRoomId, String senderId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId);
        if (!chatRoom.isGroupChatRoom() || senderId.equals(chatRoom.getMemberIdList().get(0))) {
            chatRoomRepository.deleteById(chatRoomId);
            messageRepository.deleteById(chatRoomId);
            // delete chat room id to related user
            updateChatRoomIdSet(chatRoom, false);
            return true;
        }
        else
            return false;
    }

    public boolean deleteChatRoomMember(String chatRoomId, String userId, String senderId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId);
        List<String> memberIdList = chatRoom.getMemberIdList();
        if (chatRoom.isGroupChatRoom() && (userId.equals(senderId) || senderId.equals(memberIdList.get(0)))) {
            memberIdList.remove(userId);
            chatRoom.setMemberIdList(memberIdList);
            return true;
        }
        else
            return false;
    }
}
