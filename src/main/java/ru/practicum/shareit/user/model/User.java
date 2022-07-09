package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

/**
 * Класс пользователя
 */
@Data
@Builder
public class User {
    private Integer id;
    private String name;
    private String email;
}
