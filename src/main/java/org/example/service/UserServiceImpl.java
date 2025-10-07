package org.example.service;


import org.example.errors.UserNotFoundException;
import org.example.model.User;
import org.example.store.UserStore;

import java.util.Objects;
import java.util.regex.Pattern;

public class UserServiceImpl implements UserService {
    private static final Pattern EMAIL_REG_EX =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UserStore store;

    public UserServiceImpl(UserStore store) {
        this.store = Objects.requireNonNull(store);
    }

    @Override
    public User createUser(String name, String email) {
        validateName(name);
        validateEmail(email);
        return store.create(name.trim(), email.trim().toLowerCase());
    }

    @Override
    public User getUser(long id) {
        return store.get(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User updateEmail(long id, String newEmail) {
        validateEmail(newEmail);
        return store.updateEmail(id, newEmail.trim().toLowerCase());
    }

    @Override
    public void deleteUser(long id) {
        store.delete(id);
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("name is needed");
    }

    private static void validateEmail(String email) {
        if (email == null || !EMAIL_REG_EX.matcher(email.trim()).matches())
            throw new IllegalArgumentException("invalid email");
    }
}