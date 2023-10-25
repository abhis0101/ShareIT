package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final Map<Integer, User> users;
    private int id = 1;

    private int incrementId() {
        return id++;
    }

    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) {
        user.setId(incrementId());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователя с id " + user.getId() + " не найдено");
        }
        users.put(user.getId(), user);
        return user;
    }

    public User getUserById(int userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    public void deleteUser(int userId) {
        users.remove(userId);
    }
}
