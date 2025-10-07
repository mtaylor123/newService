package org.example.store;

import org.example.model.User;

import java.util.Optional;

public interface UserStore {
    User create(String name, String email);
    Optional<User> get(long id);
    User updateEmail(long id, String newEmail);
    void delete(long id);
}
