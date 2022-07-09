package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

/**
 * Маппер для бронирования
 */
public class BookingMapper {
    /**
     * Преобразование модели в DTO
     */
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(toBookingItem(booking.getItem()))
                .booker(toUserBooking(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    /**
     * Преобразование DTO в модель
     */
    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(toItem(bookingDto.getItem()))
                .booker(toUser(bookingDto.getBooker()))
                .status(bookingDto.getStatus())
                .build();
    }

    private static BookingDto.Item toBookingItem(Item item) {
        return BookingDto.Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest())
                .build();
    }

    private static Item toItem(BookingDto.Item bookingItem) {
        return Item.builder()
                .id(bookingItem.getId())
                .name(bookingItem.getName())
                .description(bookingItem.getDescription())
                .available(bookingItem.isAvailable())
                .request(bookingItem.getRequest())
                .build();
    }

    private static BookingDto.User toUserBooking(User user) {
        return BookingDto.User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    private static User toUser(BookingDto.User bookingUser) {
        return User.builder()
                .id(bookingUser.getId())
                .name(bookingUser.getName())
                .email(bookingUser.getEmail())
                .build();
    }
}
