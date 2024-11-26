package com.example.chatserver.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.chatserver.models.ChatRoom;
import com.example.chatserver.models.User;
import com.example.chatserver.repositories.ChatRoomRepository;
import com.example.chatserver.repositories.UserRepository;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatRoomService chatRoomService;

    public boolean createUser(User user) {
        if (userRepository.existsById(user.getUserId())) {
            userRepository.save(user);
            return true;
        }
        else
            return false;
    }

    public User login(String userId, String password) {
        if (!userRepository.existsById(userId)) return null;
        User user = userRepository.findByUserId(userId);
        if (password.equals(user.getPassword()))
            return user;
        else
            return null;
    }

    public User getContact(String userId) {
        if (!userRepository.existsById(userId)) return null;
        User user = userRepository.findByUserId(userId);
        user.setPassword("");
        user.setFriendIdSet(new HashSet<>());
        user.setChatRoomIdSet(new HashSet<>());
        user.setFriendApplicationSenderIdSet(new HashSet<>());
        return user;
    }

    public Set<String> getFriendApplications(String userId) {
        return userRepository.findByUserId(userId).getFriendApplicationSenderIdSet();
    }

    public String newFriendApplication(String userId, String friendId) {
        if (!userRepository.existsById(friendId)) return "Invalid Friend ID.";
        User user = userRepository.findByUserId(userId);
        Set<String> friendApplicationSenderIdSet = user.getFriendApplicationSenderIdSet();
        if (user.getFriendIdSet().contains(friendId))
            return "Friend already exists.";
        else if (friendApplicationSenderIdSet.contains(friendId)) {
            Set<String> friendIdSet = user.getFriendIdSet();
            // if (friendIdSet == null) friendIdSet = new HashSet<>();
            friendIdSet.add(friendId);
            user.setFriendIdSet(friendIdSet);
            friendApplicationSenderIdSet.remove(friendId);
            user.setFriendApplicationSenderIdSet(friendApplicationSenderIdSet);
            userRepository.save(user);
            return "Friend application accepted.";
        }
        else {
            User friend = userRepository.findByUserId(friendId);
            friendApplicationSenderIdSet = friend.getFriendApplicationSenderIdSet();
            // if (friendApplicationSenderIdSet == null) friendApplicationSenderIdSet = new HashSet<>();
            friendApplicationSenderIdSet.add(userId);
            friend.setFriendApplicationSenderIdSet(friendApplicationSenderIdSet);
            userRepository.save(friend);
            // send application to the friend in the controller
            return "Friend application sent.";
        }
    }

    @Transactional
    public User deleteFriend(String userId, String friendId) {
        User user = userRepository.findByUserId(userId);
        Set<String> friendIdSet = user.getFriendIdSet();
        friendIdSet.remove(friendId);
        user.setFriendIdSet(friendIdSet);
        userRepository.save(user);
        // delete related chatroom
        Set<String> chatRoomIdSet = user.getChatRoomIdSet();
        for (String chatRoomId: chatRoomIdSet) {
            ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId);
            List<String> memberIdList = chatRoom.getMemberIdList();
            if (!chatRoom.isGroupChatRoom() && memberIdList.contains(userId) && memberIdList.contains(friendId)) {
                chatRoomService.deleteChatRoom(chatRoomId, userId);
                break;
            }
        }
        return user;
    }

    public boolean changePassword(String userId, String password, String newPassword, String publicKey) {
        if (!userRepository.existsById(userId)) return false;
        User user = userRepository.findByUserId(userId);
        if (user.getPassword().equals(password)) {
            user.setPassword(newPassword);
            user.setPublicKey(publicKey);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void updateUserSettings(String userId, boolean disableNotification) {
        // TODO: Logic to update user settings
    }

    public String getPublicKey(String userId) {
        return userRepository.findByUserId(userId).getPublicKey();
    }

    public Set<String> getChatRoomIdSet(String userId) {
        return userRepository.findByUserId(userId).getChatRoomIdSet();
    }
}
