package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

/**
 * Репозиторий бронирования
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {
    /**
     * Получение всех бронирований пользователя отсортированных по дате старта
     */
    Collection<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    /**
     * Получение всех текущих бронирований пользователя отсортированных по дате старта
     */
    Collection<Booking> findAllByBookerIdAndEndIsBeforeAndStartIsAfterOrderByStartDesc(long bookerId,
                                                                                       LocalDateTime end,
                                                                                       LocalDateTime start);

    /**
     * Получение всех бронирований пользователя из прошлого отсортированных по дате старта
     */
    Collection<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(long bookerId, LocalDateTime end);

    /**
     * Получение всех бронирований пользователя из будущего отсортированных по дате старта
     */
    Collection<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime start);

    /**
     * Получение всех бронирований пользователя по статусу отсортированных по дате старта
     */
    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    /**
     * Получение бронирований для всех вещей текущего пользователя отсортированных по дате старта
     */
    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(long userId);

    /**
     * Получение всех текущих бронирований для всех вещей текущего пользователя отсортированных по дате старта
     */
    Collection<Booking> findAllByItemOwnerIdAndEndIsBeforeAndStartIsAfterOrderByStartDesc(long bookerId,
                                                                                          LocalDateTime end,
                                                                                          LocalDateTime start);

    /**
     * Получение бронирований для всех вещей текущего пользователя из прошлого отсортированных по дате старта
     */
    Collection<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(long bookerId, LocalDateTime end);

    /**
     * Получение бронирований для всех вещей текущего пользователя из будущего отсортированных по дате старта
     */
    Collection<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime start);

    /**
     * Получение бронирований для всех вещей текущего пользователя по статусу отсортированных по дате старта
     */
    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    /**
     * Получение последнего бронирования веши
     */
    Optional<Booking> findFirstByItemOwnerIdAndStatusOrderByEnd(long userId, BookingStatus status);

    /**
     * Получение следующего бронирования вещи
     */
    Optional<Booking> findFirstByItemOwnerIdAndStatusOrderByEndDesc(long userId, BookingStatus status);

    /**
     * Получение бронирования по вещи и пользователю
     */
    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusAndStartBefore(long userId, long itemId,
                                                                          BookingStatus status, LocalDateTime now);
}
