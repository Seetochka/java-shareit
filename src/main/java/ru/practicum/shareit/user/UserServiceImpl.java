package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    /**
     * Создание пользователя
     */
    @Override
    public UserDto createUser(UserDto userDto) throws DuplicateEmailException, ValidationException {
        checkEmail(userDto.getEmail());

        User user = userRepository.createUser(UserMapper.toUser(userDto));

        log.info("CreateUser. Создан пользователь с id {}", user.getId());
        return UserMapper.toUserDto(user);
    }

    /**
     * Получение пользователя по id
     */
    @Override
    public UserDto getUserById(int userId) throws ObjectNotFountException {
        checkUserId(userId);

        User user = userRepository.getUserById(userId);

        return UserMapper.toUserDto(user);
    }

    /**
     * Получение всех пользователей
     */
    public Collection<UserDto> getAll() {
        Collection<User> users = userRepository.getAll().values();

        Collection<UserDto> usersDto = new ArrayList<>();

        for (User user : users) {
            usersDto.add(UserMapper.toUserDto(user));
        }

        return usersDto;
    }

    /**
     * Обновление пользователя
     */
    @Override
    public UserDto updateUser(int userId, UserDto userDto)
            throws ObjectNotFountException, ValidationException, DuplicateEmailException {
        checkUserId(userId);

        if (userDto.getEmail() != null) {
            checkEmail(userDto.getEmail());
        }

        User user = userRepository.updateUser(userId, UserMapper.toUser(userDto));

        log.info("UpdateUser. Обновлены данные пользователя с id {}", user.getId());
        return UserMapper.toUserDto(user);
    }

    /**
     * Удаление пользователя
     */
    @Override
    public int deleteUser(int userId) throws ObjectNotFountException {
        checkUserId(userId);

        int userDeletedId = userRepository.deleteUser(userId);

        log.info("DeleteUser. Удален пользователь с id {}", userDeletedId);
        return userDeletedId;
    }

    /**
     * Проверка существования пользователя по id
     */
    @Override
    public void checkUserId(int userId) throws ObjectNotFountException {
        if (!userRepository.getAll().containsKey(userId)) {
            throw new ObjectNotFountException(String.format("Пользователь с id %d не существует", userId),
                    "CheckUserId");
        }
    }

    private void checkEmail(String email) throws DuplicateEmailException, ValidationException {
        if (email == null) {
            throw new ValidationException("Не передан обязательный параметр email", "CheckEmail");
        }

        for (User user : userRepository.getAll().values()) {
            if (Objects.equals(user.getEmail(), email)) {
                throw new DuplicateEmailException(String.format("Пользователь с email %s уже существует", email),
                        "CheckEmail");
            }
        }
    }
}
