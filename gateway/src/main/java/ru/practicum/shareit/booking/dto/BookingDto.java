package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * DTO бронирования
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;
    @NonNull
    private LocalDateTime start;
    @NonNull
    private LocalDateTime end;
    @NonNull
    private Long itemId;
}
