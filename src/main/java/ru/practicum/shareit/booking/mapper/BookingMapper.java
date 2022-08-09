package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.dto.GottenBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

/**
 * Маппер для бронирования
 */
@Component
public class BookingMapper {
    /**
     * Преобразование модели в DTO
     */
    public CreatedBookingDto toCreatedBookingDto(Booking booking) {
        return new CreatedBookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId()
        );
    }

    /**
     * Преобразование модели в DTO
     */
    public GottenBookingDto toGottenBookingDto(Booking booking) {
        return new GottenBookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                toGottenBookingDtoBookingItem(booking.getItem()),
                toUserGottenBookingDtoBooking(booking.getBooker()),
                booking.getStatus()
        );
    }

    /**
     * Преобразование DTO в модель
     */
    public Booking toBooking(CreatedBookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                new Item(bookingDto.getItemId(), null, null, null, null, null, null, null, null),
                null,
                null
        );
    }

    /**
     * Преобразование DTO в модель
     */
    public Booking toBooking(GottenBookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                toItem(bookingDto.getItem()),
                toUser(bookingDto.getBooker()),
                bookingDto.getStatus()
        );
    }

    private GottenBookingDto.Item toGottenBookingDtoBookingItem(Item item) {
        return new GottenBookingDto.Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    private Item toItem(GottenBookingDto.Item bookingItem) {
        return new Item(
                bookingItem.getId(),
                bookingItem.getName(),
                bookingItem.getDescription(),
                bookingItem.isAvailable(),
                null,
                null,
                null,
                null,
                null
        );
    }

    private GottenBookingDto.User toUserGottenBookingDtoBooking(User user) {
        return new GottenBookingDto.User(user.getId(), user.getName(), user.getEmail());
    }

    private User toUser(GottenBookingDto.User bookingUser) {
        return new User(bookingUser.getId(), bookingUser.getName(), bookingUser.getEmail());
    }
}
