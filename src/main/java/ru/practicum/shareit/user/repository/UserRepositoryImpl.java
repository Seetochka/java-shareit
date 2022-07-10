package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    /**
     * Проверка существования пользователя по id
     */
    @Override
    public void checkUserId(int userId) throws ObjectNotFountException {
        if (!getAll().containsKey(userId)) {
            throw new ObjectNotFountException(String.format("Пользователь с id %d не существует", userId),
                    "CheckUserId");
        }
    }

    /**
     * Проверяет email на дублирование
     */
    @Override
    public void checkEmail(String email) throws DuplicateEmailException, ValidationException {
        if (!StringUtils.hasText(email)) {
            throw new ValidationException("Не передан обязательный параметр email", "CheckEmail");
        }

        for (User user : getAll().values()) {
            if (Objects.equals(user.getEmail(), email)) {
                throw new DuplicateEmailException(String.format("Пользователь с email %s уже существует", email),
                        "CheckEmail");
            }
        }
    }

    private static int getNextId() {
        return globalId++;
    }
}
