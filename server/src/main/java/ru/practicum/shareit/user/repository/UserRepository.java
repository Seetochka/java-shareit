package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

/**
 * Интерфейс репозитория пользователя
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
