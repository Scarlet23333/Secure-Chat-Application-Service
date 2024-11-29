package com.example.chatserver.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.chatserver.models.ChatRoom;
import com.example.chatserver.models.User;
import com.example.chatserver.repositories.ChatRoomRepository;
import com.example.chatserver.repositories.MessageRepository;
import com.example.chatserver.repositories.UserRepository;

@Service
public class ChatRoomService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private void updateChatRoomIdSet(String chatRoomId, String userId, boolean isAdd) {
        if (userRepository.existsById(userId)) {
            User user = userRepository.findByUserId(userId);
            Set<String> chatRoomIdSet = user.getChatRoomIdSet();
            if (isAdd)
                chatRoomIdSet.add(chatRoomId);
            else
                chatRoomIdSet.remove(chatRoomId);
            user.setChatRoomIdSet(chatRoomIdSet);
            userRepository.save(user);
            messagingTemplate.convertAndSend("/topic/chatroom/" + userId, "Chat room updated.");
        }
    }

    private void updateAllChatRoomIdSet(ChatRoom chatRoom, boolean isAdd) {
        List<String> memberIdList = chatRoom.getMemberIdList();
        String chatRoomId = chatRoom.getChatRoomId();
        for (String userId: memberIdList) {
            updateChatRoomIdSet(chatRoomId, userId, isAdd);
        }
    }

    @Transactional
    public String createChatRoom(ChatRoom chatRoom) {
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        // add chat room id to related user
        updateAllChatRoomIdSet(savedChatRoom, true);
        return savedChatRoom.getChatRoomId();
    }

    public ChatRoom getChatRoom(String chatRoomId) {
        return chatRoomRepository.findByChatRoomId(chatRoomId);
    }

    @Transactional
    public void addChatRoomMember(String chatRoomId, String userId) {
        if (chatRoomRepository.existsById(chatRoomId)) {
            ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId);
            List<String> memberIdList = chatRoom.getMemberIdList();
            if (chatRoom.isGroupChatRoom() && !memberIdList.contains(userId)) {
                memberIdList.add(userId);
                chatRoom.setMemberIdList(memberIdList);
                chatRoomRepository.save(chatRoom);
                updateChatRoomIdSet(chatRoomId, userId, true);
            }
        }
    }

    @Transactional
    public boolean deleteChatRoom(String chatRoomId, String senderId) {
        if (!chatRoomRepository.existsById(chatRoomId)) return false;
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId);
        if (!chatRoom.isGroupChatRoom() || senderId.equals(chatRoom.getMemberIdList().get(0))) {
            chatRoomRepository.deleteById(chatRoomId);
            messageRepository.deleteByChatRoomId(chatRoomId);
            // delete chat room id to related user
            updateAllChatRoomIdSet(chatRoom, false);
            return true;
        }
        else
            return false;
    }

    @Transactional
    public boolean deleteChatRoomMember(String chatRoomId, String userId, String senderId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId);
        List<String> memberIdList = chatRoom.getMemberIdList();
        if (chatRoom.isGroupChatRoom() && (userId.equals(senderId) || senderId.equals(memberIdList.get(0)))) {
            memberIdList.remove(userId);
            chatRoom.setMemberIdList(memberIdList);
            chatRoomRepository.save(chatRoom);
            updateChatRoomIdSet(chatRoomId, userId, false);
            return true;
        }
        else
            return false;
    }
}
