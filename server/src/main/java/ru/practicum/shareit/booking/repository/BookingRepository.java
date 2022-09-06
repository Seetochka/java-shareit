package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий бронирования
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {
    /**
     * Получение всех бронирований пользователя отсортированных по дате старта
     */
    List<Booking> findAllByBookerId(long userId, Pageable page);

    /**
     * Получение всех текущих бронирований пользователя отсортированных по дате старта
     */
    List<Booking> findAllByBookerIdAndEndIsAfterAndStartIsBefore(long bookerId, LocalDateTime end, LocalDateTime start,
                                                                 Pageable page);

    /**
     * Получение всех бронирований пользователя из прошлого отсортированных по дате старта
     */
    List<Booking> findAllByBookerIdAndEndIsBefore(long bookerId, LocalDateTime end, Pageable page);

    /**
     * Получение всех бронирований пользователя из будущего отсортированных по дате старта
     */
    List<Booking> findAllByBookerIdAndStartIsAfter(long bookerId, LocalDateTime start, Pageable page);

    /**
     * Получение всех бронирований пользователя по статусу отсортированных по дате старта
     */
    List<Booking> findAllByBookerIdAndStatus(long bookerId, BookingStatus status, Pageable page);

    /**
     * Получение бронирований для всех вещей текущего пользователя отсортированных по дате старта
     */
    List<Booking> findAllByItemOwnerId(long userId, Pageable page);

    /**
     * Получение всех текущих бронирований для всех вещей текущего пользователя отсортированных по дате старта
     */
    List<Booking> findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(long bookerId,
                                                                    LocalDateTime end,
                                                                    LocalDateTime start,
                                                                    Pageable page);

    /**
     * Получение бронирований для всех вещей текущего пользователя из прошлого отсортированных по дате старта
     */
    List<Booking> findAllByItemOwnerIdAndEndIsBefore(long bookerId, LocalDateTime end, Pageable page);

    /**
     * Получение бронирований для всех вещей текущего пользователя из будущего отсортированных по дате старта
     */
    List<Booking> findAllByItemOwnerIdAndStartIsAfter(long bookerId, LocalDateTime start, Pageable page);

    /**
     * Получение бронирований для всех вещей текущего пользователя по статусу отсортированных по дате старта
     */
    List<Booking> findAllByItemOwnerIdAndStatus(long bookerId, BookingStatus status, Pageable page);

    /**
     * Получение последнего бронирования веши
     */
    Optional<Booking> findFirstByItemIdAndStatusOrderByEnd(long itemId, BookingStatus status);

    /**
     * Получение следующего бронирования вещи
     */
    Optional<Booking> findFirstByItemIdAndStatusOrderByEndDesc(long itemId, BookingStatus status);

    /**
     * Получение бронирования по вещи и пользователю
     */
    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusAndStartBefore(long userId, long itemId,
                                                                          BookingStatus status, LocalDateTime now);
}
