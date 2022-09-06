package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.enums.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(HEADER_USER_ID) long userId,
                                                @RequestBody @Valid BookingDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.createBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setApproved(@RequestHeader(HEADER_USER_ID) long userId,
                                              @PathVariable long bookingId,
                                              @RequestParam boolean approved) {
        log.info("Approve booking {}, userId={}, approved={}", bookingId, userId, approved);
        return bookingClient.setApproved(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(HEADER_USER_ID) long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBookerId(@RequestHeader(HEADER_USER_ID) long userId,
                                                   @RequestParam(defaultValue = "ALL") BookingState state,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getAllByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwnerId(@RequestHeader(HEADER_USER_ID) long userId,
                                                  @RequestParam(defaultValue = "ALL") BookingState state,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Get owner booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getAllByOwnerId(userId, state, from, size);
    }
}
