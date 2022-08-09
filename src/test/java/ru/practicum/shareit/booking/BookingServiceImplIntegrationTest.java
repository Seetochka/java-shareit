package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingState;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    private static final User mockUser1 = new User(1L, "User1", "1@user.com");
    private static final User mockUser2 = new User(2L, "User2", "2@user.com");

    private static final Item mockItem1 = new Item(1L, "Item1", "ItemDesc1", true, mockUser1,
            null, null, null, null);

    private static final Booking mockBooking1 = new Booking(1L, LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(5), mockItem1, mockUser2, BookingStatus.WAITING);
    private static final Booking mockBooking2 = new Booking(2L, LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(4), mockItem1, mockUser2, BookingStatus.WAITING);

    @Test
    void testGetAllByBookerId() throws Exception {
        userService.createUser(mockUser1);
        userService.createUser(mockUser2);
        itemService.createItem(mockUser1.getId(), mockItem1);
        bookingService.createBooking(mockUser2.getId(), mockBooking1);
        bookingService.createBooking(mockUser2.getId(), mockBooking2);

        Collection<Booking> bookings = bookingService.getAllByBookerId(mockUser2.getId(), BookingState.WAITING, 0, 20);

        assertThat(bookings, hasSize(2));
        assertThat(bookings.stream().findFirst().isPresent(), is(true));
        assertThat(bookings.stream().findFirst().get().getId(), equalTo(mockBooking2.getId()));
        assertThat(bookings.stream().findFirst().get().getStart(), equalTo(mockBooking2.getStart()));
        assertThat(bookings.stream().findFirst().get().getEnd(), equalTo(mockBooking2.getEnd()));
    }

    @Test
    void testGetAllByBookerIdWrongUser() {
        Exception exception = assertThrows(ObjectNotFountException.class, () ->
                bookingService.getAllByBookerId(mockUser2.getId(), BookingState.WAITING, 0, 20));

        assertEquals("Пользователь с id 2 не существует", exception.getMessage());
    }
}
