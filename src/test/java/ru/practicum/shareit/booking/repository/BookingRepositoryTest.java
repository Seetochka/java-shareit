package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {
    private static final int PAGE = 0;
    private static final int SIZE = 20;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private static final User mockUser1 = new User(1L, "User1", "1@user.com");
    private static final User mockUser2 = new User(2L, "User2", "2@user.com");
    private static final Item mockItem1 = new Item(1L, "Item1", "ItemDesc1", true, mockUser1, null, null, null, null);
    private static final Item mockItem2 = new Item(null, "Item2", "ItemDesc2", true, mockUser2, null, null, null, null);
    private static final Booking mockBooking1 = new Booking(1L, LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(6), mockItem1, mockUser2, BookingStatus.WAITING);
    private static final Booking mockBooking2 = new Booking(2L, LocalDateTime.now().plusMonths(1),
            LocalDateTime.now().plusMonths(1).plusDays(4), mockItem1, mockUser2, BookingStatus.APPROVED);
    private static final Booking mockBooking3 = new Booking(null, LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(15), mockItem2, mockUser1, BookingStatus.WAITING);
    private static final Booking mockBooking4 = new Booking(null, LocalDateTime.now().plusDays(5),
            LocalDateTime.now().plusDays(11), mockItem2, mockUser1, BookingStatus.REJECTED);

    @Test
    void testFindAllByBookerId() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);
        bookingRepository.save(mockBooking1);
        bookingRepository.save(mockBooking2);
        bookingRepository.save(mockBooking3);
        bookingRepository.save(mockBooking4);

        Sort sortById = Sort.by(Sort.Direction.DESC, "start");
        Pageable page = PageRequest.of(PAGE, SIZE, sortById);

        Collection<Booking> bookings = bookingRepository.findAllByBookerId(2L, page);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(2).contains(mockBooking1, mockBooking2);
    }

    @Test
    void testFindAllByBookerIdAndEndIsAfterAndStartIsBefore() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);
        bookingRepository.save(mockBooking1);
        bookingRepository.save(mockBooking2);
        bookingRepository.save(mockBooking3);
        bookingRepository.save(mockBooking4);

        Sort sortById = Sort.by(Sort.Direction.DESC, "start");
        Pageable page = PageRequest.of(PAGE, SIZE, sortById);

        Collection<Booking> bookings = bookingRepository.findAllByBookerIdAndEndIsAfterAndStartIsBefore(2L,
                LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(15), page);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(1).contains(mockBooking1);
    }

    @Test
    void testFindAllByBookerIdAndEndIsBefore() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);
        bookingRepository.save(mockBooking1);
        bookingRepository.save(mockBooking2);
        bookingRepository.save(mockBooking3);
        bookingRepository.save(mockBooking4);

        Sort sortById = Sort.by(Sort.Direction.DESC, "start");
        Pageable page = PageRequest.of(PAGE, SIZE, sortById);

        Collection<Booking> bookings = bookingRepository.findAllByBookerIdAndEndIsBefore(2L,
                LocalDateTime.now().plusDays(15), page);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(1).contains(mockBooking1);
    }

    @Test
    void testFindAllByBookerIdAndStartIsAfter() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);
        bookingRepository.save(mockBooking1);
        bookingRepository.save(mockBooking2);
        bookingRepository.save(mockBooking3);
        bookingRepository.save(mockBooking4);

        Sort sortById = Sort.by(Sort.Direction.DESC, "start");
        Pageable page = PageRequest.of(PAGE, SIZE, sortById);

        Collection<Booking> bookings = bookingRepository.findAllByBookerIdAndStartIsAfter(2L,
                LocalDateTime.now().minusDays(3), page);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(2).contains(mockBooking1, mockBooking2);
    }

    @Test
    void testFindAllByBookerIdAndStatus() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);
        bookingRepository.save(mockBooking1);
        bookingRepository.save(mockBooking2);
        bookingRepository.save(mockBooking3);
        bookingRepository.save(mockBooking4);

        Sort sortById = Sort.by(Sort.Direction.DESC, "start");
        Pageable page = PageRequest.of(PAGE, SIZE, sortById);

        Collection<Booking> bookings = bookingRepository.findAllByBookerIdAndStatus(1L,
                BookingStatus.REJECTED, page);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(1).contains(mockBooking4);
    }

    @Test
    void testFindAllByItemOwnerId() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);
        bookingRepository.save(mockBooking1);
        bookingRepository.save(mockBooking2);
        bookingRepository.save(mockBooking3);
        bookingRepository.save(mockBooking4);

        Sort sortById = Sort.by(Sort.Direction.DESC, "start");
        Pageable page = PageRequest.of(PAGE, SIZE, sortById);

        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerId(1L, page);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(2).contains(mockBooking1, mockBooking2);
    }

    @Test
    void testFindAllByItemOwnerIdAndEndIsAfterAndStartIsBefore() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);
        bookingRepository.save(mockBooking1);
        bookingRepository.save(mockBooking2);
        bookingRepository.save(mockBooking3);
        bookingRepository.save(mockBooking4);

        Sort sortById = Sort.by(Sort.Direction.DESC, "start");
        Pageable page = PageRequest.of(PAGE, SIZE, sortById);

        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(1L,
                LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(10), page);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(1).contains(mockBooking1);
    }

    @Test
    void testFindAllByItemOwnerIdAndEndIsBefore() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);
        bookingRepository.save(mockBooking1);
        bookingRepository.save(mockBooking2);
        bookingRepository.save(mockBooking3);
        bookingRepository.save(mockBooking4);

        Sort sortById = Sort.by(Sort.Direction.DESC, "start");
        Pageable page = PageRequest.of(PAGE, SIZE, sortById);

        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(1L,
                LocalDateTime.now().plusMonths(1).plusDays(4), page);

        assertThat(bookings).hasSize(2).contains(mockBooking1, mockBooking2);
    }

    @Test
    void testFindAllByItemOwnerIdAndStartIsAfter() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);
        bookingRepository.save(mockBooking1);
        bookingRepository.save(mockBooking2);
        bookingRepository.save(mockBooking3);
        bookingRepository.save(mockBooking4);

        Sort sortById = Sort.by(Sort.Direction.DESC, "start");
        Pageable page = PageRequest.of(PAGE, SIZE, sortById);

        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(2L,
                LocalDateTime.now().plusDays(2), page);

        assertThat(bookings).hasSize(2).contains(mockBooking3, mockBooking4);
    }

    @Test
    void testFindAllByItemOwnerIdAndStatus() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);
        bookingRepository.save(mockBooking1);
        bookingRepository.save(mockBooking2);
        bookingRepository.save(mockBooking3);
        bookingRepository.save(mockBooking4);

        Sort sortById = Sort.by(Sort.Direction.DESC, "start");
        Pageable page = PageRequest.of(PAGE, SIZE, sortById);

        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatus(1L,
                BookingStatus.WAITING, page);

        assertThat(bookings).hasSize(1).contains(mockBooking1);
    }

    @Test
    void testFindFirstByItemOwnerIdAndStatusOrderByEnd() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);
        bookingRepository.save(mockBooking1);
        bookingRepository.save(mockBooking2);
        bookingRepository.save(mockBooking3);
        bookingRepository.save(mockBooking4);

        Optional<Booking> bookings = bookingRepository.findFirstByItemIdAndStatusOrderByEnd(1L,
                BookingStatus.APPROVED);

        assertThat(bookings).isPresent();
        assertThat(bookings.get()).isEqualTo(mockBooking2);
    }

    @Test
    void testFindFirstByItemOwnerIdAndStatusOrderByEndDesc() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);
        bookingRepository.save(mockBooking1);
        bookingRepository.save(mockBooking2);
        bookingRepository.save(mockBooking3);
        bookingRepository.save(mockBooking4);

        Optional<Booking> bookings = bookingRepository.findFirstByItemIdAndStatusOrderByEndDesc(2L,
                BookingStatus.REJECTED);

        assertThat(bookings).isPresent();
        assertThat(bookings.get()).isEqualTo(mockBooking4);
    }

    @Test
    void testFindFirstByBookerIdAndItemIdAndStatusAndStartBefore() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);
        bookingRepository.save(mockBooking1);
        bookingRepository.save(mockBooking2);
        bookingRepository.save(mockBooking3);
        bookingRepository.save(mockBooking4);

        Optional<Booking> bookings = bookingRepository.findFirstByBookerIdAndItemIdAndStatusAndStartBefore(1L, 2L,
                BookingStatus.WAITING, LocalDateTime.now().plusDays(7));

        assertThat(bookings).isPresent();
        assertThat(bookings.get()).isEqualTo(mockBooking3);
    }
}
