package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.enums.Status;

import java.time.LocalDate;

/**
 * DTO бронирования
 */
@Data
@Builder
public class BookingDto {
    private Integer id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User booker;
    private Status status;

    @Data
    @Builder
    public static class Item {
        private Integer id;
        private String name;
        private String description;
        private boolean available;
        private String request;
    }

    @Data
    @Builder
    public static class User {
        private Integer id;
        private String name;
        private String email;
    }
}
