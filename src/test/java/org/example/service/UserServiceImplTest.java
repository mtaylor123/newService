package org.example.service;

import org.example.errors.UserNotFoundException;
import org.example.model.User;
import org.example.store.InMemoryUserStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {
    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserServiceImpl(new InMemoryUserStore());
    }

    @Test
    void createAndGetUser_ok() {
        User u = service.createUser("user1", "user1@gmail.com");
        assertNotNull(u);
        assertTrue(u.getId() > 0);
        User fetched = service.getUser(u.getId());
        assertEquals("user1", fetched.getName());
        assertEquals("user1@gmail.com", fetched.getEmail());
    }

    @Test
    void getUser_notFound() {
        assertThrows(UserNotFoundException.class, () -> service.getUser(9999));
    }

    @Test
    void updateEmail_ok() {
        User u = service.createUser("user2", "user2@gmail.com");
        User updated = service.updateEmail(u.getId(), "user2newemail@gmail.com");
        assertEquals("user2newemail@gmail.com", updated.getEmail());
        assertEquals("user2newemail@gmail.com", service.getUser(u.getId()).getEmail());
    }

    @Test
    void updateEmail_notFound() {
        assertThrows(UserNotFoundException.class, () -> service.updateEmail(42, "user@gmail.com"));
    }

    @Test
    void deleteUser_ok() {
        User u = service.createUser("user3", "user3@gmail.com");
        assertDoesNotThrow(() -> service.deleteUser(u.getId()));
        assertThrows(UserNotFoundException.class, () -> service.getUser(u.getId()));
    }

    @Test
    void deleteUser_notFound() {
        assertThrows(UserNotFoundException.class, () -> service.deleteUser(123));
    }

    @Test
    void createUser_validation() {
        assertThrows(IllegalArgumentException.class, () -> service.createUser("", "a@gmail.com"));
        assertThrows(IllegalArgumentException.class, () -> service.createUser("newUser", "invalidemail"));
    }

    @Test
    void updateEmail_validation() {
        User u = service.createUser("newuser2", "newuser2@gmail.com");
        assertThrows(IllegalArgumentException.class, () -> service.updateEmail(u.getId(), "invalid"));
    }
}