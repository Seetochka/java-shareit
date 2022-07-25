package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Создание пользователя
     */
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.save(userMapper.toUser(userDto));

        log.info("CreateUser. Создан пользователь с id {}", user.getId());
        return userMapper.toUserDto(user);
    }

    /**
     * Получение пользователя по id
     */
    @Override
    public UserDto getUserById(long userId) throws ObjectNotFountException {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFountException(
                String.format("Пользователь с id %d не существует", userId),
                "GetUserById")
        );

        return userMapper.toUserDto(user);
    }

    /**
     * Получение всех пользователей
     */
    public Collection<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    /**
     * Обновление пользователя
     */
    @Override
    public UserDto updateUser(long userId, UserDto userDto) throws ObjectNotFountException {
        User userUpdated = userMapper.toUser(getUserById(userId));

        if (userDto.getEmail() != null) {
            userUpdated.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            userUpdated.setName(userDto.getName());
        }

        log.info("UpdateUser. Обновлены данные пользователя с id {}", userUpdated.getId());
        return userMapper.toUserDto(userRepository.save(userUpdated));
    }

    /**
     * Удаление пользователя
     */
    @Override
    public void deleteUser(long userId) throws ObjectNotFountException {
        getUserById(userId);

        userRepository.deleteById(userId);

        log.info("DeleteUser. Удален пользователь с id {}", userId);
    }
}
