package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

/**
 * Сервис пользователей
 */
public interface UserService {
    /**
     * Создание пользователя
     */
    User createUser(User user);

    /**
     * Получение пользователя по id
     */
    User getUserById(long userId) throws ObjectNotFountException;

    /**
     * Получение всех пользователей
     */
    Collection<User> getAll();

    /**
     * Обновление пользователя
     */
    User updateUser(long userId, User user) throws ObjectNotFountException;

    /**
     * Удаление пользователя
     */
    void deleteUser(long userId) throws ObjectNotFountException;

    /**
     * Проверка существования пользователя по id
     */
    void checkUserExistsById(long userId) throws ObjectNotFountException;
}
