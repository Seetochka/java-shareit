package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO вещи, которую могут брать в аренду
 */
@Data
@Builder
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private String request;
}
