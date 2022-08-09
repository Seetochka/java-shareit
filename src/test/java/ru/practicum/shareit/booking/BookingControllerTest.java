package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.dto.GottenBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingState;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.UserHaveNoRightsException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @MockBean
    private BookingService bookingService;
    @MockBean
    private BookingMapper bookingMapper;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final User mockUser1 = new User(1L, "User1", "1@user.com");
    private final User mockUser2 = new User(2L, "User2", "2@user.com");

    private final Item mockItem1 = new Item(1L, "Item1", "ItemDesc1", true, mockUser1,
            null, null, null, null);

    private final Booking mockBooking = new Booking(1L, LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(4), mockItem1, mockUser2, BookingStatus.WAITING);
    private final CreatedBookingDto mockCreatedBookingDto = new CreatedBookingDto(1L,
            LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(8), 1L);
    private final GottenBookingDto mockGottenBookingDto = new GottenBookingDto(1L,
            LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(6),
            new GottenBookingDto.Item(1L, null, null, true),
            new GottenBookingDto.User(1L, null, null), BookingStatus.WAITING);

    @Test
    void testCreateItem() throws Exception {
        when(bookingService.createBooking(any(Long.class), any()))
                .thenReturn(mockBooking);
        doReturn(mockCreatedBookingDto).when(bookingMapper).toCreatedBookingDto(any());

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(mockCreatedBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockCreatedBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(mockCreatedBookingDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(mockCreatedBookingDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.itemId", is(mockCreatedBookingDto.getItemId()), Long.class));
    }

    @Test
    void testSetApproved() throws Exception {
        when(bookingService.setApproved(any(Long.class), any(Long.class), any(Boolean.class)))
                .thenReturn(mockBooking);
        doReturn(mockGottenBookingDto).when(bookingMapper).toGottenBookingDto(any());

        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(mockGottenBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1)
                        .queryParam("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockGottenBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(mockGottenBookingDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(mockGottenBookingDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(mockGottenBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(mockGottenBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(mockGottenBookingDto.getStatus().toString())));
    }

    @Test
    void testSetApprovedWithoutRights() throws Exception {
        when(bookingService.setApproved(any(Long.class), any(Long.class), any(Boolean.class)))
                .thenThrow(new UserHaveNoRightsException("TestSetApprovedWithoutRights", "TestSetApprovedWithoutRights"));

        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(mockGottenBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1)
                        .queryParam("approved", "true"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("TestSetApprovedWithoutRights")));
    }

    @Test
    void testGetItemById() throws Exception {
        when(bookingService.getBookingById(any(Long.class), any(Long.class)))
                .thenReturn(mockBooking);
        doReturn(mockGottenBookingDto).when(bookingMapper).toGottenBookingDto(any());

        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(mockGottenBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockGottenBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(mockGottenBookingDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(mockGottenBookingDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(mockGottenBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(mockGottenBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(mockGottenBookingDto.getStatus().toString())));
    }

    @Test
    void testGetAllByBookerId() throws Exception {
        when(bookingService.getAllByBookerId(any(Long.class), any(BookingState.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(mockBooking));
        doReturn(mockGottenBookingDto).when(bookingMapper).toGottenBookingDto(any());

        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(mockGottenBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(mockGottenBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(mockGottenBookingDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(mockGottenBookingDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].item.id", is(mockGottenBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(mockGottenBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(mockGottenBookingDto.getStatus().toString())));
    }

    @Test
    void testGetAllByBookerIdByStateUnsupported() throws Exception {
        when(bookingService.getAllByBookerId(any(Long.class), any(BookingState.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(mockBooking));
        doReturn(mockGottenBookingDto).when(bookingMapper).toGottenBookingDto(any());

        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(mockGottenBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1)
                        .queryParam("state", "UNSUPPORTED_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Unknown state: UNSUPPORTED_STATUS")));
    }

    @Test
    void testGetAllByOwnerId() throws Exception {
        when(bookingService.getAllByOwnerId(any(Long.class), any(BookingState.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(mockBooking));
        doReturn(mockGottenBookingDto).when(bookingMapper).toGottenBookingDto(any());

        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(mockGottenBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(mockGottenBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(mockGottenBookingDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(mockGottenBookingDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].item.id", is(mockGottenBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(mockGottenBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(mockGottenBookingDto.getStatus().toString())));
    }

    @Test
    void testGetAllByOwnerIdByStateUnsupported() throws Exception {
        when(bookingService.getAllByOwnerId(any(Long.class), any(BookingState.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(mockBooking));
        doReturn(mockGottenBookingDto).when(bookingMapper).toGottenBookingDto(any());

        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(mockGottenBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1)
                        .queryParam("state", "UNSUPPORTED_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Unknown state: UNSUPPORTED_STATUS")));
    }
}
