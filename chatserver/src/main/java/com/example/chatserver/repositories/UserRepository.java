package com.example.chatserver.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.chatserver.models.User;

public interface UserRepository extends MongoRepository<User, String> {
    // boolean existsById(String userId);
    User findByUserId(String userId);
    // User saveUser(User user);
}
