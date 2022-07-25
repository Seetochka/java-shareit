package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.dto.GottenBookingDto;
import ru.practicum.shareit.enums.BookingState;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.UserHaveNoRightsException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;

/**
 * Сервис бронирования
 */
public interface BookingService {
    /**
     * Создание бронирования
     */
    CreatedBookingDto createBooking(long userId, CreatedBookingDto booking)
            throws ObjectNotFountException, ValidationException, UserHaveNoRightsException;

    /**
     * Подтверждение или отклонение запроса на бронирование
     */
    GottenBookingDto setApproved(long userId, long bookingId, boolean approved)
            throws ValidationException, ObjectNotFountException, UserHaveNoRightsException;

    /**
     * Получение бронирования по id
     */
    GottenBookingDto getBookingById(long userId, long bookingId)
            throws ObjectNotFountException, UserHaveNoRightsException;

    /**
     * Получение всех бронирований текущего пользователя
     */
    Collection<GottenBookingDto> getAllByBookerId(long userId, BookingState state) throws ObjectNotFountException;

    /**
     * Получение бронирований для всех вещей текущего пользователя
     */
    Collection<GottenBookingDto> getAllByOwnerId(long userId, BookingState state) throws ObjectNotFountException;
}
