package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

/**
 * Сервис пользователей
 */
public interface UserService {
    /**
     * Создание пользователя
     */
    UserDto createUser(UserDto userDto);

    /**
     * Получение пользователя по id
     */
    UserDto getUserById(long userId) throws ObjectNotFountException;

    /**
     * Получение всех пользователей
     */
    Collection<UserDto> getAll();

    /**
     * Обновление пользователя
     */
    UserDto updateUser(long userId, UserDto userDto) throws ObjectNotFountException;

    /**
     * Удаление пользователя
     */
    void deleteUser(long userId) throws ObjectNotFountException;

    /**
     * Проверка существования пользователя по id
     */
    void checkUserExistsById(long userId) throws ObjectNotFountException;
}
