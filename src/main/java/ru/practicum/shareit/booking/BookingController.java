package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.dto.GottenBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingState;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.UserHaveNoRightsException;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Контроллер отвечающий за бронирование
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public CreatedBookingDto createItem(@RequestHeader(HEADER_USER_ID) long userId,
                                        @Valid @RequestBody CreatedBookingDto bookingDto)
            throws ObjectNotFountException, ValidationException, UserHaveNoRightsException {
        Booking booking = bookingService.createBooking(userId, bookingMapper.toBooking(bookingDto));

        return bookingMapper.toCreatedBookingDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public GottenBookingDto setApproved(@RequestHeader(HEADER_USER_ID) long userId,
                                        @PathVariable long bookingId, @RequestParam boolean approved)
            throws ValidationException, ObjectNotFountException, UserHaveNoRightsException {
        return bookingMapper.toGottenBookingDto((bookingService.setApproved(userId, bookingId, approved)));
    }

    @GetMapping("/{bookingId}")
    public GottenBookingDto getItemById(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long bookingId)
            throws ObjectNotFountException, UserHaveNoRightsException {
        return bookingMapper.toGottenBookingDto(bookingService.getBookingById(userId, bookingId));
    }

    @GetMapping
    public Collection<GottenBookingDto> getAllByBookerId(@RequestHeader(HEADER_USER_ID) long userId,
                                                         @RequestParam(defaultValue = "ALL") BookingState state)
            throws ObjectNotFountException {
        return bookingService.getAllByBookerId(userId, state)
                .stream()
                .map(bookingMapper::toGottenBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public Collection<GottenBookingDto> getAllByOwnerId(@RequestHeader(HEADER_USER_ID) long userId,
                                                        @RequestParam(defaultValue = "ALL") BookingState state)
            throws ObjectNotFountException {
        return bookingService.getAllByOwnerId(userId, state)
                .stream()
                .map(bookingMapper::toGottenBookingDto)
                .collect(Collectors.toList());
    }
}
