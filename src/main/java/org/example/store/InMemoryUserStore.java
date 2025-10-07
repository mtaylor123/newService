package org.example.store;

import org.example.errors.UserNotFoundException;
import org.example.model.User;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserStore implements UserStore {
    private final ConcurrentMap<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Override
    public User create(String name, String email) {
        long id = idGen.getAndIncrement();
        User u = new User(id, name, email);
        users.put(id, u);
        return u;
    }

    @Override
    public Optional<User> get(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User updateEmail(long id, String newEmail) {
        User updated = users.compute(id, (k, existing) -> {
            if (existing == null) throw new UserNotFoundException(id);
            return existing.withEmail(newEmail);
        });
        return updated;
    }

    @Override
    public void delete(long id) {
        if (users.remove(id) == null) {
            throw new UserNotFoundException(id);
        }
    }
}