package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

/**
 * Сервис пользователей
 */
public interface UserService {
    /**
     * Создание пользователя
     */
    UserDto createUser(UserDto userDto) throws DuplicateEmailException, ValidationException;

    /**
     * Получение пользователя по id
     */
    UserDto getUserById(int userId) throws ObjectNotFountException;

    /**
     * Получение всех пользователей
     */
    Collection<UserDto> getAll();

    /**
     * Обновление пользователя
     */
    UserDto updateUser(int userId, UserDto userDto)
            throws ObjectNotFountException, ValidationException, DuplicateEmailException;

    /**
     * Удаление пользователя
     */
    int deleteUser(int userId) throws ObjectNotFountException;

    /**
     * Проверка существования пользователя по id
     */
    void checkUserId(int userId) throws ObjectNotFountException;
}
