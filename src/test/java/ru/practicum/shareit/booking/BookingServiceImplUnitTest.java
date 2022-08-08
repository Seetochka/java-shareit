package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.UserHaveNoRightsException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplUnitTest {
    private final User mockUser1 = new User(1L, "User1", "1@user.com");
    private final User mockUser2 = new User(2L, "User2", "2@user.com");

    private final Item mockItem1 = new Item(1L, "Item1", "ItemDesc1", true, mockUser1,
            null, null, null, null);
    private final Item mockItemUnAvailable = new Item(1L, "Item1", "ItemDesc1", false, mockUser1,
            null, null, null, null);

    private final Booking mockBooking1 = new Booking(1L, LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(8), mockItem1, mockUser2, BookingStatus.WAITING);
    private final Booking mockBookingUnAvailable = new Booking(1L, LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(5), mockItemUnAvailable, mockUser2, BookingStatus.WAITING);
    private final Booking mockBookingEndFromLast = new Booking(1L, LocalDateTime.now().plusDays(2),
            LocalDateTime.now().minusDays(5), mockItem1, mockUser2, BookingStatus.WAITING);
    private final Booking mockBookingStartFromLast = new Booking(1L, LocalDateTime.now().minusDays(2),
            LocalDateTime.now().plusDays(5), mockItem1, mockUser2, BookingStatus.WAITING);
    private final Booking mockBookingStartAfterEnd = new Booking(1L, LocalDateTime.now().plusDays(5),
            LocalDateTime.now().plusDays(2), mockItem1, mockUser2, BookingStatus.WAITING);
    private final Booking mockBookingWrongUser = new Booking(1L, LocalDateTime.now().plusDays(5),
            LocalDateTime.now().plusDays(15), mockItem1, mockUser1, BookingStatus.WAITING);
    private final Booking mockBookingApproved1 = new Booking(1L, LocalDateTime.now().plusDays(4),
            LocalDateTime.now().plusDays(8), mockItem1, mockUser2, BookingStatus.APPROVED);
    private final Booking mockBookingRejected1 = new Booking(1L, LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(7), mockItem1, mockUser2, BookingStatus.REJECTED);
    private final Booking mockBooking2 = new Booking(2L, LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(5), mockItem1, mockUser2, BookingStatus.WAITING);
    private final Booking mockBooking3 = new Booking(3L, LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(5), mockItem1, mockUser2, BookingStatus.WAITING);

    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;

    @Mock
    private BookingRepository bookingRepository;

    private BookingService bookingService;

    private MockitoSession session;

    @BeforeEach
    void setUp() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        bookingService = new BookingServiceImpl(userService, itemService, bookingRepository);
    }

    @AfterEach
    void tearDown() {
        session.finishMocking();
    }

    @Test
    void testCreateBooking() throws ObjectNotFountException, ValidationException, UserHaveNoRightsException {
        Mockito.when(itemService.getItemById(Mockito.any(Long.class), Mockito.any(Long.class)))
                .thenReturn(mockItem1);
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(mockBooking1);

        Booking booking = bookingService.createBooking(2L, mockBooking1);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(mockBooking1);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(mockBooking1.getStart()));
        assertThat(booking.getEnd(), equalTo(mockBooking1.getEnd()));
    }

    @Test
    void testCreateBookingFailValidationBooking() throws ObjectNotFountException {
        Mockito.when(itemService.getItemById(Mockito.any(Long.class), Mockito.any(Long.class)))
                .thenReturn(mockItem1);

        Exception exception1 = assertThrows(ValidationException.class, () ->
                bookingService.createBooking(2L, mockBookingStartFromLast));

        assertEquals("Нельзя указать дату начала бронирования из прошлого", exception1.getMessage());

        Exception exception2 = assertThrows(ValidationException.class, () ->
                bookingService.createBooking(2L, mockBookingEndFromLast));

        assertEquals("Нельзя указать дату конца бронирования из прошлого", exception2.getMessage());

        Exception exception3 = assertThrows(ValidationException.class, () ->
                bookingService.createBooking(2L, mockBookingStartAfterEnd));

        assertEquals("Нельзя указать дату конца бронирования до начала", exception3.getMessage());

        Exception exception4 = assertThrows(UserHaveNoRightsException.class, () ->
                bookingService.createBooking(1L, mockBookingWrongUser));

        assertEquals("Нельзя забронировать вещь владельцу веши", exception4.getMessage());
    }

    @Test
    void testCreateBookingFailValidationItem() throws ObjectNotFountException {
        Mockito.when(itemService.getItemById(Mockito.any(Long.class), Mockito.any(Long.class)))
                .thenReturn(mockItemUnAvailable);

        Exception exception1 = assertThrows(ValidationException.class, () ->
                bookingService.createBooking(2L, mockBookingUnAvailable));

        assertEquals("Нельзя забронировать недоступную вещь", exception1.getMessage());
    }

    @Test
    void testSetApproved() throws ValidationException, UserHaveNoRightsException, ObjectNotFountException {
        Mockito.when(bookingRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(mockBooking1));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(mockBookingApproved1);

        Booking booking = bookingService.setApproved(1L, mockBooking1.getId(), true);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(mockBooking1);

        assertThat(booking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void testSetApprovedWithStatusNotWaiting() {
        Mockito.when(bookingRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(mockBookingApproved1));

        Exception exception = assertThrows(ValidationException.class, () ->
                bookingService.setApproved(1L, mockBookingApproved1.getId(), true));

        assertEquals("Бронирование с id 1 не ожидает подтверждения", exception.getMessage());
    }

    @Test
    void testSetApprovedWrongUser() {
        Mockito.when(bookingRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(mockBooking1));

        Exception exception = assertThrows(UserHaveNoRightsException.class, () ->
                bookingService.setApproved(2L, mockBooking1.getId(), true));

        assertEquals("Пользователь с id 2 не может менять статус данной вещи", exception.getMessage());
    }

    @Test
    void testSetNotApproved() throws ValidationException, UserHaveNoRightsException, ObjectNotFountException {
        Mockito.when(bookingRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(mockBooking2));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(mockBookingRejected1);

        Booking booking = bookingService.setApproved(1L, mockBooking2.getId(), false);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(mockBooking2);

        assertThat(booking.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void testGetBookingById() throws UserHaveNoRightsException, ObjectNotFountException {
        Mockito.when(bookingRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(mockBooking1));

        Booking booking = bookingService.getBookingById(1L, 1L);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findById(1L);

        assertThat(booking.getId(), equalTo(mockBooking1.getId()));
        assertThat(booking.getStatus(), equalTo(mockBooking1.getStatus()));
        assertThat(booking.getStart(), equalTo(mockBooking1.getStart()));
        assertThat(booking.getEnd(), equalTo(mockBooking1.getEnd()));
        assertThat(booking.getItem(), equalTo(mockBooking1.getItem()));
    }

    @Test
    void testGetBookingByWrongId() {
        Mockito.when(bookingRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ObjectNotFountException.class, () ->
                bookingService.getBookingById(1L, 1L));

        assertEquals("Бронирование с id 1 не существует", exception.getMessage());
    }

    @Test
    void testGetBookingByIdWrongUser() {
        Mockito.when(bookingRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(mockBooking1));

        Exception exception = assertThrows(UserHaveNoRightsException.class, () ->
                bookingService.getBookingById(3L, 1L));

        assertEquals("Пользователь с id 3 не может просматривать данную вещь", exception.getMessage());
    }

    @Test
    void testGetAllByBookerIdByStateAll() throws ObjectNotFountException, UnsupportedStatusException {
        Mockito.when(bookingRepository.findAllByBookerId(Mockito.any(Long.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking1, mockBooking2, mockBooking3));

        Collection<Booking> bookings = bookingService.getAllByBookerId(1L, "ALL", 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerId(Mockito.any(Long.class), Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(3));
        assertThat(bookings, equalTo(List.of(mockBooking1, mockBooking2, mockBooking3)));
    }

    @Test
    void testGetAllByBookerIdByStateCurrent() throws ObjectNotFountException, UnsupportedStatusException {
        Mockito.when(bookingRepository.findAllByBookerIdAndEndIsAfterAndStartIsBefore(Mockito.any(Long.class),
                        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking2, mockBooking3));

        Collection<Booking> bookings = bookingService.getAllByBookerId(1L, "CURRENT", 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndEndIsAfterAndStartIsBefore(Mockito.any(Long.class),
                        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(2));
        assertThat(bookings, equalTo(List.of(mockBooking2, mockBooking3)));
    }

    @Test
    void testGetAllByBookerIdByStatePast() throws ObjectNotFountException, UnsupportedStatusException {
        Mockito.when(bookingRepository.findAllByBookerIdAndEndIsBefore(Mockito.any(Long.class),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking1, mockBooking2));

        Collection<Booking> bookings = bookingService.getAllByBookerId(1L, "PAST", 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndEndIsBefore(Mockito.any(Long.class), Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(2));
        assertThat(bookings, equalTo(List.of(mockBooking1, mockBooking2)));
    }

    @Test
    void testGetAllByBookerIdByStateFuture() throws ObjectNotFountException, UnsupportedStatusException {
        Mockito.when(bookingRepository.findAllByBookerIdAndStartIsAfter(Mockito.any(Long.class),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking1, mockBooking3));

        Collection<Booking> bookings = bookingService.getAllByBookerId(1L, "FUTURE", 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStartIsAfter(Mockito.any(Long.class), Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(2));
        assertThat(bookings, equalTo(List.of(mockBooking1, mockBooking3)));
    }

    @Test
    void testGetAllByBookerIdByStateWaiting() throws ObjectNotFountException, UnsupportedStatusException {
        Mockito.when(bookingRepository.findAllByBookerIdAndStatus(Mockito.any(Long.class),
                        Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking3));

        Collection<Booking> bookings = bookingService.getAllByBookerId(1L, "WAITING", 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStatus(Mockito.any(Long.class), Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(1));
        assertThat(bookings, equalTo(List.of(mockBooking3)));
    }

    @Test
    void testGetAllByBookerIdByStateRejected() throws ObjectNotFountException, UnsupportedStatusException {
        Mockito.when(bookingRepository.findAllByBookerIdAndStatus(Mockito.any(Long.class),
                        Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking2));

        Collection<Booking> bookings = bookingService.getAllByBookerId(1L, "REJECTED", 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStatus(Mockito.any(Long.class), Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(1));
        assertThat(bookings, equalTo(List.of(mockBooking2)));
    }

    @Test
    void testGetAllByBookerIdByStateUnsupported() {
        Exception exception = assertThrows(UnsupportedStatusException.class, () ->
                bookingService.getAllByBookerId(1L, "UNSUPPORTED_STATUS", 0, 20));

        assertEquals("Получен неподдерживаемый статус UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void testGetAllByOwnerIdByStateAll() throws ObjectNotFountException, UnsupportedStatusException {
        Mockito.when(bookingRepository.findAllByItemOwnerId(Mockito.any(Long.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking1, mockBooking2, mockBooking3));

        Collection<Booking> bookings = bookingService.getAllByOwnerId(1L, "ALL", 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerId(Mockito.any(Long.class), Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(3));
        assertThat(bookings, equalTo(List.of(mockBooking1, mockBooking2, mockBooking3)));
    }

    @Test
    void testGetAllByOwnerIdByStateCurrent() throws ObjectNotFountException, UnsupportedStatusException {
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(Mockito.any(Long.class),
                        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking2, mockBooking3));

        Collection<Booking> bookings = bookingService.getAllByOwnerId(1L, "CURRENT", 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(Mockito.any(Long.class),
                        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(2));
        assertThat(bookings, equalTo(List.of(mockBooking2, mockBooking3)));
    }

    @Test
    void testGetAllByOwnerIdByStatePast() throws ObjectNotFountException, UnsupportedStatusException {
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndEndIsBefore(Mockito.any(Long.class),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking1, mockBooking2));

        Collection<Booking> bookings = bookingService.getAllByOwnerId(1L, "PAST", 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndEndIsBefore(Mockito.any(Long.class), Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(2));
        assertThat(bookings, equalTo(List.of(mockBooking1, mockBooking2)));
    }

    @Test
    void testGetAllByOwnerIdByStateFuture() throws ObjectNotFountException, UnsupportedStatusException {
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(Mockito.any(Long.class),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking1, mockBooking3));

        Collection<Booking> bookings = bookingService.getAllByOwnerId(1L, "FUTURE", 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndStartIsAfter(Mockito.any(Long.class), Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(2));
        assertThat(bookings, equalTo(List.of(mockBooking1, mockBooking3)));
    }

    @Test
    void testGetAllByOwnerIdByStateWaiting() throws ObjectNotFountException, UnsupportedStatusException {
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatus(Mockito.any(Long.class),
                        Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking3));

        Collection<Booking> bookings = bookingService.getAllByOwnerId(1L, "WAITING", 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndStatus(Mockito.any(Long.class), Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(1));
        assertThat(bookings, equalTo(List.of(mockBooking3)));
    }

    @Test
    void testGetAllByOwnerIdByStateRejected() throws ObjectNotFountException, UnsupportedStatusException {
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatus(Mockito.any(Long.class),
                        Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking2));

        Collection<Booking> bookings = bookingService.getAllByOwnerId(1L, "REJECTED", 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndStatus(Mockito.any(Long.class), Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(1));
        assertThat(bookings, equalTo(List.of(mockBooking2)));
    }

    @Test
    void testGetAllByOwnerIdByStateUnsupported() {
        Exception exception = assertThrows(UnsupportedStatusException.class, () ->
                bookingService.getAllByOwnerId(1L, "UNSUPPORTED_STATUS", 0, 20));

        assertEquals("Получен неподдерживаемый статус UNSUPPORTED_STATUS", exception.getMessage());
    }
}
