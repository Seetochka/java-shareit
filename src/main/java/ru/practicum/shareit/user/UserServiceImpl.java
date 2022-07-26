package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    /**
     * Создание пользователя
     */
    @Override
    public User createUser(User user) {
        user = userRepository.save(user);

        log.info("CreateUser. Создан пользователь с id {}", user.getId());
        return user;
    }

    /**
     * Получение пользователя по id
     */
    @Override
    public User getUserById(long userId) throws ObjectNotFountException {
        return userRepository.findById(userId).orElseThrow(() -> new ObjectNotFountException(
                String.format("Пользователь с id %d не существует", userId),
                "GetUserById")
        );
    }

    /**
     * Получение всех пользователей
     */
    public Collection<User> getAll() {
        return userRepository.findAll();
    }

    /**
     * Обновление пользователя
     */
    @Override
    public User updateUser(long userId, User user) throws ObjectNotFountException {
        User userUpdated = getUserById(userId);

        Optional.ofNullable(user.getEmail()).ifPresent(userUpdated::setEmail);
        Optional.ofNullable(user.getName()).ifPresent(userUpdated::setName);

        log.info("UpdateUser. Обновлены данные пользователя с id {}", userUpdated.getId());
        return userRepository.save(userUpdated);
    }

    /**
     * Удаление пользователя
     */
    @Override
    public void deleteUser(long userId) throws ObjectNotFountException {
        checkUserExistsById(userId);

        userRepository.deleteById(userId);

        log.info("DeleteUser. Удален пользователь с id {}", userId);
    }

    /**
     * Проверка существования пользователя по id
     */
    @Override
    public void checkUserExistsById(long userId) throws ObjectNotFountException {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFountException(
                    String.format("Пользователь с id %d не существует", userId),
                    "CheckUserExistsById"
            );
        }
    }
}
