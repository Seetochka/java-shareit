package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

/**
 * Маппер для пользователя
 */
@Component
public class UserMapper {
    /**
     * Преобразование модели в DTO
     */
    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(),user.getName(), user.getEmail());
    }

    /**
     * Преобразование DTO в модель
     */
    public User toUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }
}
