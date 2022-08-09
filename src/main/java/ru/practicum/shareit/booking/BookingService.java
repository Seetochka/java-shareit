package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;
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
    Booking createBooking(long userId, Booking booking)
            throws ObjectNotFountException, ValidationException, UserHaveNoRightsException;

    /**
     * Подтверждение или отклонение запроса на бронирование
     */
    Booking setApproved(long userId, long bookingId, boolean approved)
            throws ValidationException, ObjectNotFountException, UserHaveNoRightsException;

    /**
     * Получение бронирования по id
     */
    Booking getBookingById(long userId, long bookingId)
            throws ObjectNotFountException, UserHaveNoRightsException;

    /**
     * Получение всех бронирований текущего пользователя
     */
    Collection<Booking> getAllByBookerId(long userId, BookingState state, int from, int size)
            throws ObjectNotFountException;

    /**
     * Получение бронирований для всех вещей текущего пользователя
     */
    Collection<Booking> getAllByOwnerId(long userId, BookingState state, int from, int size)
            throws ObjectNotFountException;
}
