package ru.practicum.shareit.item;

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
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplUnitTest {
    private final User mockUser1 = new User(1L, "User1", "1@user.com");
    private final User mockUser2 = new User(2L, "User2", "2@user.com");

    private final ItemRequest mockItemRequest = new ItemRequest(1L, "ItemRequestDesk1", mockUser2,
            LocalDateTime.now(), null);

    private final Item mockItem1 = new Item(1L, "Item1", "ItemDesc1", true, mockUser1,
            mockItemRequest, null, null, null);

    private final Item mockItemWithoutName = new Item(1L, null, "ItemDesc1", true, mockUser1,
            null, null, null, null);
    private final Item mockItemWithoutDesc = new Item(1L, "Item1", null, true, mockUser1,
            null, null, null, null);
    private final Item mockItemWithoutAvailable = new Item(1L, "Item1", "ItemDesc1", null, mockUser1,
            null, null, null, null);
    private final Item mockUpdatedItem1 = new Item(1L, "Item1Update", "ItemDesc1", true, mockUser1,
            null, null, null, null);
    private final Item mockItem2 = new Item(2L, "Item1", "ItemDesc2", true, mockUser2,
            null, null, null, null);

    private final Booking mockBooking = new Booking(1L, LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(8), mockItem1, mockUser2, BookingStatus.APPROVED);

    private final Comment mockComment = new Comment(1L, "Comment1", mockItem1, mockUser2, LocalDateTime.now());

    @Mock
    private UserService userService;
    @Mock
    private ItemRequestService itemRequestService;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;

    private ItemService itemService;

    private MockitoSession session;


    @BeforeEach
    void setUp() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        itemService = new ItemServiceImpl(userService, itemRequestService, bookingRepository, itemRepository,
                commentRepository);
    }

    @AfterEach
    void tearDown() {
        session.finishMocking();
    }

    @Test
    void testCreateItem() throws ValidationException, ObjectNotFountException {
        Mockito.when(userService.getUserById(Mockito.any(Long.class)))
                .thenReturn(mockUser1);
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(mockItem1);

        Item item = itemService.createItem(1L, mockItem1);

        Mockito.verify(itemRepository, Mockito.times(1))
                .save(mockItem1);

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(mockItem1.getName()));
        assertThat(item.getDescription(), equalTo(mockItem1.getDescription()));
        assertThat(item.getAvailable(), equalTo(mockItem1.getAvailable()));
        assertThat(item.getOwner(), equalTo(mockItem1.getOwner()));
    }

    @Test
    void testCreateItemFailValidation() throws ObjectNotFountException {
        Mockito.when(userService.getUserById(Mockito.any(Long.class)))
                .thenReturn(mockUser1);

        Exception exception1 = assertThrows(ValidationException.class, () ->
                itemService.createItem(mockUser1.getId(), mockItemWithoutName));

        assertEquals("Не заполнено поле name", exception1.getMessage());

        Exception exception2 = assertThrows(ValidationException.class, () ->
                itemService.createItem(mockUser1.getId(), mockItemWithoutDesc));

        assertEquals("Не заполнено поле description", exception2.getMessage());

        Exception exception3 = assertThrows(ValidationException.class, () ->
                itemService.createItem(mockUser1.getId(), mockItemWithoutAvailable));

        assertEquals("Не заполнено поле available", exception3.getMessage());
    }

    @Test
    void testGetItemById() throws ObjectNotFountException {
        Mockito.when(itemRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(mockItem1));

        Item item = itemService.getItemById(1L, 1L);

        Mockito.verify(itemRepository, Mockito.times(1))
                .findById(1L);

        assertThat(item.getId(), equalTo(1L));
        assertThat(item.getName(), equalTo(mockItem1.getName()));
        assertThat(item.getDescription(), equalTo(mockItem1.getDescription()));
        assertThat(item.getAvailable(), equalTo(mockItem1.getAvailable()));
        assertThat(item.getOwner(), equalTo(mockItem1.getOwner()));
        assertThat(item.getRequest(), equalTo(mockItemRequest));
        assertThat(item.getComments(), nullValue());
    }

    @Test
    void testGetItemByWrongId() {
        Mockito.when(itemRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ObjectNotFountException.class, () ->
                itemService.getItemById(1L, 1L));

        assertEquals("Вещь с id 1 не существует", exception.getMessage());
    }

    @Test
    void testGetAllByUserId() throws ObjectNotFountException {
        Mockito.when(itemRepository.findAllByOwnerId(Mockito.any(Long.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockItem1, mockItem2));

        Collection<Item> items = itemService.getAllByUserId(1L, 0, 20);

        Mockito.verify(itemRepository, Mockito.times(1))
                .findAllByOwnerId(Mockito.any(Long.class), Mockito.any(Pageable.class));

        assertThat(items, hasSize(2));
        assertThat(items, equalTo(List.of(mockItem1, mockItem2)));
    }

    @Test
    void testUpdateItem() throws ObjectNotFountException {
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(mockUpdatedItem1);
        Mockito.when(itemRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(mockItem1));

        mockItem1.setName("Item1Update");

        Item item = itemService.updateItem(1L, mockItem1.getId(), mockItem1);

        Mockito.verify(itemRepository, Mockito.times(1))
                .save(mockItem1);

        assertThat(item.getId(), equalTo(mockUpdatedItem1.getId()));
        assertThat(item.getName(), equalTo(mockUpdatedItem1.getName()));
    }

    @Test
    void testUpdateItemWrongUser() {
        Mockito.when(itemRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(mockItem1));

        mockItem1.setName("Item1Update");

        Exception exception = assertThrows(ObjectNotFountException.class, () ->
                itemService.updateItem(2L, mockItem1.getId(), mockItem1));

        assertEquals("Передан неверный владелец вещи", exception.getMessage());
    }

    @Test
    void testDeleteItem() throws ObjectNotFountException {
        Mockito.when(itemRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(true);

        itemService.deleteItem(1L, 1L);

        Mockito.verify(itemRepository, Mockito.times(1))
                .deleteById(1L);
    }

    @Test
    void testSearchItemByText() {
        Mockito.when(itemRepository.search(Mockito.any(String.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockItem2));

        Collection<Item> items = itemService.searchItemByText("Desc2", 0, 20);

        Mockito.verify(itemRepository, Mockito.times(1))
                .search(Mockito.any(String.class), Mockito.any(Pageable.class));

        assertThat(items, hasSize(1));
        assertThat(items, equalTo(List.of(mockItem2)));
    }

    @Test
    void testCreateComment() throws ObjectNotFountException, ValidationException {
        Mockito.when(userService.getUserById(Mockito.any(Long.class)))
                .thenReturn(mockUser2);
        Mockito.when(itemRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(mockItem1));
        Mockito.when(bookingRepository.findFirstByBookerIdAndItemIdAndStatusAndStartBefore(Mockito.any(Long.class),
                        Mockito.any(Long.class), Mockito.any(BookingStatus.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.of(mockBooking));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(mockComment);

        Comment comment = itemService.createComment(2L, mockItem1.getId(), mockComment);

        Mockito.verify(commentRepository, Mockito.times(1))
                .save(mockComment);

        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getText(), equalTo(mockComment.getText()));
        assertThat(comment.getItem(), equalTo(mockComment.getItem()));
        assertThat(comment.getAuthor(), equalTo(mockComment.getAuthor()));
    }

    @Test
    void testCreateCommentFailValidation() throws ObjectNotFountException {
        Mockito.when(userService.getUserById(Mockito.any(Long.class)))
                .thenReturn(mockUser2);
        Mockito.when(itemRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(mockItem1));
        Mockito.when(bookingRepository.findFirstByBookerIdAndItemIdAndStatusAndStartBefore(Mockito.any(Long.class),
                        Mockito.any(Long.class), Mockito.any(BookingStatus.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        Exception exception1 = assertThrows(ValidationException.class, () ->
                itemService.createComment(mockUser1.getId(), mockItem1.getId(), mockComment));

        assertEquals("Пользователь с id 1 не брал вещь с id 1 в аренду", exception1.getMessage());
    }

    @Test
    void testCheckItemExistsById() throws ObjectNotFountException {
        Mockito.when(itemRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(true);

        itemService.checkItemExistsById(1L);

        Mockito.verify(itemRepository, Mockito.times(1))
                .existsById(1L);
    }

    @Test
    void testCheckItemNotExistsById() {
        Mockito.when(itemRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(false);

        Exception exception = assertThrows(ObjectNotFountException.class, () -> itemService.checkItemExistsById(1L));

        assertEquals("Вещь с id 1 не существует", exception.getMessage());
    }
}
