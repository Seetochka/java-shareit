package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

/**
 * Класс вещи, которую могут брать в аренду
 */
@Data
@Builder
public class Item {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private String request;
}
