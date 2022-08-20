package ru.practicum.shareit.request;

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
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
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
class ItemRequestServiceImplUnitTest {
    private final User mockUser1 = new User(1L, "User1", "1@user.com");
    private final User mockUser2 = new User(2L, "User2", "2@user.com");

    private final ItemRequest mockItemRequest1 = new ItemRequest(1L, "ItemRequestDesk1", mockUser1,
            LocalDateTime.now(), null);
    private final ItemRequest mockItemRequestWithoutDesc = new ItemRequest(1L, null, null,
            LocalDateTime.now(), null);
    private final ItemRequest mockItemRequest2 = new ItemRequest(2L, "ItemRequestDesk2", mockUser2,
            LocalDateTime.now(), null);
    private final ItemRequest mockItemRequest3 = new ItemRequest(3L, "ItemRequestDesk3", mockUser1,
            LocalDateTime.now(), null);

    @Mock
    private UserService userService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private ItemRequestService itemRequestService;

    private MockitoSession session;

    @BeforeEach
    void setUp() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        itemRequestService = new ItemRequestServiceImpl(userService, itemRequestRepository);
    }

    @AfterEach
    void tearDown() {
        session.finishMocking();
    }

    @Test
    void testCreateItemRequest() throws ObjectNotFountException, ValidationException {
        Mockito.when(userService.getUserById(Mockito.any(Long.class)))
                .thenReturn(mockUser2);
        Mockito.when(itemRequestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(mockItemRequest1);

        ItemRequest itemRequest = itemRequestService.createItemRequest(2L, mockItemRequest1);

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .save(mockItemRequest1);

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(mockItemRequest1.getDescription()));
        assertThat(itemRequest.getCreated(), equalTo(mockItemRequest1.getCreated()));
    }

    @Test
    void testCreateItemRequestWithoutDesc() throws ObjectNotFountException {
        Mockito.when(userService.getUserById(Mockito.any(Long.class)))
                .thenReturn(mockUser2);

        Exception exception = assertThrows(ValidationException.class, () ->
                itemRequestService.createItemRequest(mockUser1.getId(), mockItemRequestWithoutDesc));

        assertEquals("Не заполнено поле description", exception.getMessage());
    }

    @Test
    void testGetItemRequestsByUser() throws ObjectNotFountException {
        Mockito.when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(Mockito.any(Long.class)))
                .thenReturn(List.of(mockItemRequest1, mockItemRequest3));

        Collection<ItemRequest> itemRequests = itemRequestService.getItemRequestsByUser(1L);

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequestorIdOrderByCreatedDesc(1L);

        assertThat(itemRequests, hasSize(2));
        assertThat(itemRequests, equalTo(List.of(mockItemRequest1, mockItemRequest3)));
    }

    @Test
    void testGetAllItemRequest() throws ObjectNotFountException {
        Mockito.when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(Mockito.any(Long.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockItemRequest1, mockItemRequest2, mockItemRequest3));

        Collection<ItemRequest> itemRequests = itemRequestService.getAllItemRequest(1L, 0, 20);

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequestorIdNotOrderByCreatedDesc(Mockito.any(Long.class), Mockito.any(Pageable.class));

        assertThat(itemRequests, hasSize(3));
        assertThat(itemRequests, equalTo(List.of(mockItemRequest1, mockItemRequest2, mockItemRequest3)));
    }

    @Test
    void testGetItemRequestById() throws ObjectNotFountException {
        Mockito.when(itemRequestRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(mockItemRequest1));

        ItemRequest itemRequests = itemRequestService.getItemRequestById(1L, 1L);

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findById(1L);

        assertThat(itemRequests.getId(), equalTo(1L));
        assertThat(itemRequests.getDescription(), equalTo(mockItemRequest1.getDescription()));
        assertThat(itemRequests.getCreated(), equalTo(mockItemRequest1.getCreated()));
    }

    @Test
    void testGetItemRequestByWrongId() {
        Mockito.when(itemRequestRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ObjectNotFountException.class, () ->
                itemRequestService.getItemRequestById(1L, 1L));

        assertEquals("Запроса вещи с id 1 не существует", exception.getMessage());
    }

    @Test
    void testCheckItemRequestExistsById() throws ObjectNotFountException {
        Mockito.when(itemRequestRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(true);

        itemRequestService.checkItemRequestExistsById(1L);

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .existsById(1L);
    }

    @Test
    void testCheckItemRequestNotExistsById() {
        Mockito.when(itemRequestRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(false);

        Exception exception = assertThrows(ObjectNotFountException.class, () ->
                itemRequestService.checkItemRequestExistsById(1L));

        assertEquals("Запроса вещи с id 1 не существует", exception.getMessage());
    }
}
