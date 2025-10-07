package org.example.service;

import org.example.model.User;

public interface UserService {
    User createUser(String name, String email);
    User getUser(long id);
    User updateEmail(long id, String newEmail);
    void deleteUser(long id);
}
