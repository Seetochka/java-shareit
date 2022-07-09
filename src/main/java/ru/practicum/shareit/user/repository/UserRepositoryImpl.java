package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserRepositoryImpl implements UserRepository {
    private static int globalId = 1;

    private final Map<Integer, User> users = new HashMap<>();

    /**
     * Создание пользователя
     */
    @Override
    public User createUser(User user) {
        user.setId(getNextId());

        users.put(user.getId(), user);

        return user;
    }

    /**
     * Получение пользователя по id
     */
    @Override
    public User getUserById(int userId) {
        return users.get(userId);
    }

    /**
     * Получение всех пользователей
     */
    @Override
    public Map<Integer, User> getAll() {
        return users;
    }

    /**
     * Обновление пользователя
     */
    @Override
    public User updateUser(int userId, User user) {
        User userUpdated = getUserById(userId);

        if (user.getEmail() != null) {
            userUpdated.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            userUpdated.setName(user.getName());
        }

        return userUpdated;
    }

    /**
     * Удаление пользователя
     */
    @Override
    public int deleteUser(int userId) {
        User user = users.remove(userId);

        return user.getId();
    }

    private static int getNextId() {
        return globalId++;
    }
}
