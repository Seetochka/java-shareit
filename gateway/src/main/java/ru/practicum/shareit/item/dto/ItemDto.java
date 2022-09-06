package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
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
    @NotBlank
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private Boolean available;
    private Long owner;
    private Long requestId;
    private Booking lastBooking;
    private Booking nextBooking;
    Collection<Comment> comments = new ArrayList<>();

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
