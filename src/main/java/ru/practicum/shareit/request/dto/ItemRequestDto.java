package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO запроса вещи
 */
@Data
@Builder
public class ItemRequestDto {
    private Integer id;
    private String description;
    private User requestor;
    private LocalDateTime created;

    @Data
    @Builder
    public static class User {
        private Integer id;
        private String name;
        private String email;
    }
}
