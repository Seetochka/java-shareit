package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Map;

/**
 * Интерфейс репозитория пользователя
 */
public interface UserRepository {
    /**
     * Создание пользователя
     */
    User createUser(User user);

    /**
     * Получение пользователя по id
     */
    User getUserById(int userId);

    /**
     * Получение всех пользователей
     */
    Map<Integer, User> getAll();

    /**
     * Обновление пользователя
     */
    User updateUser(int userId, User user);

    /**
     * Удаление пользователя
     */
    int deleteUser(int userId);
}
