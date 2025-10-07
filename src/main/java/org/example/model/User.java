package org.example.model;

import java.util.Objects;

public final class User {
    private final long id;
    private final String name;
    private final String email;

    public User(long id, String name, String email) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public User withEmail(String newEmail) {
        return new User(this.id, this.name, newEmail);
    }
}