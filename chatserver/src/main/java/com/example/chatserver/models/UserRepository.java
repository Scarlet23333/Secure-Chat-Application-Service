package com.example.chatserver.models;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    // boolean existsById(String userId);
    User findByUserId(String userId);
    // User saveUser(User user);
}
