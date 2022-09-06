package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.enums.BookingStatus;

import java.time.LocalDateTime;

/**
 * DTO бронирования для получения бонирования
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GottenBookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private GottenBookingDto.Item item;
    private GottenBookingDto.User booker;
    private BookingStatus status;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private Long id;
        private String name;
        private String description;
        private boolean available;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private Long id;
        private String name;
        private String email;
    }
}
