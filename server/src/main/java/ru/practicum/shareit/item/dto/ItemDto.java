package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * DTO вещи, которую могут брать в аренду
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Long requestId;
    private Booking lastBooking;
    private Booking nextBooking;
    Collection<Comment> comments = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private Long id;
        private String name;
        private String email;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Booking {
        private Long id;
        private Long bookerId;
        private LocalDateTime start;
        private LocalDateTime end;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Comment {
        Long id;
        String text;
        String authorName;
    }
}
