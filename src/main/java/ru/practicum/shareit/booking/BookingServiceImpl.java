package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.BookingState;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.UserHaveNoRightsException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;

    private final BookingRepository bookingRepository;

    /**
     * Создание бронирования
     */
    @Override
    public Booking createBooking(long userId, Booking booking)
            throws ObjectNotFountException, ValidationException, UserHaveNoRightsException {
        userService.checkUserExistsById(userId);

        Item item = itemService.getItemById(userId, booking.getItem().getId());

        if (!item.getAvailable()) {
            throw new ValidationException("Нельзя забронировать недоступную вещь", "CreateBooking");
        }
        if (item.getOwner().getId() == userId) {
            throw new UserHaveNoRightsException("Нельзя забронировать вещь владельцу веши", "CreateBooking");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Нельзя указать дату начала бронирования из прошлого", "CreateBooking");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Нельзя указать дату конца бронирования из прошлого", "CreateBooking");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Нельзя указать дату конца бронирования до начала", "CreateBooking");
        }

        booking.setBooker(new User(userId, null, null));
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        log.info("CreateBooking. Создано бронирование с id {}", booking.getId());
        return bookingRepository.save(booking);
    }

    /**
     * Подтверждение или отклонение запроса на бронирование
     */
    @Override
    public Booking setApproved(long userId, long bookingId, boolean approved)
            throws ValidationException, ObjectNotFountException, UserHaveNoRightsException {
        Booking booking = getBookingById(userId, bookingId);

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException(
                    String.format("Бронирование с id %d не ожидает подтверждения", bookingId),
                    "SetStatus"
            );
        }

        if (booking.getItem().getOwner().getId() != userId) {
            throw new UserHaveNoRightsException(
                    String.format("Пользователь с id %d не может менять статус данной вещи", userId),
                    "SetApproved"
            );
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return bookingRepository.save(booking);
    }

    /**
     * Получение бронирования по id
     */
    @Override
    public Booking getBookingById(long userId, long bookingId)
            throws ObjectNotFountException, UserHaveNoRightsException {
        userService.checkUserExistsById(userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ObjectNotFountException(
                String.format("Бронирование с id %d не существует", bookingId),
                "GetBookingById"
        ));

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new UserHaveNoRightsException(
                    String.format("Пользователь с id %d не может просматривать данную вещь", userId),
                    "GetBookingById"
            );
        }

        return booking;
    }

    /**
     * Получение всех бронирований текущего пользователя
     */
    @Override
    public Collection<Booking> getAllByBookerId(long userId, BookingState state)
            throws ObjectNotFountException {
        userService.checkUserExistsById(userId);

        Collection<Booking> result;

        switch (state) {
            case ALL:
                result = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                result = bookingRepository.findAllByBookerIdAndEndIsBeforeAndStartIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                result = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId,
                        LocalDateTime.now());
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now());
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                result = Collections.emptyList();
        }

        return result;
    }

    /**
     * Получение бронирований для всех вещей текущего пользователя
     */
    @Override
    public Collection<Booking> getAllByOwnerId(long userId, BookingState state)
            throws ObjectNotFountException {
        userService.checkUserExistsById(userId);

        Collection<Booking> result;

        switch (state) {
            case ALL:
                result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                result = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeAndStartIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                result = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId,
                        LocalDateTime.now());
                break;
            case FUTURE:
                result = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now());
                break;
            case WAITING:
                result = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                result = Collections.emptyList();
        }

        return result;
    }
}
