package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceImplIntegrationTest {
    private final UserService userService;
    private final ItemRequestService itemRequestService;

    private final User mockUser1 = new User(1L, "User1", "1@user.com");
    private final ItemRequest mockItemRequest = new ItemRequest(1L, "ItemRequestDesk1", mockUser1,
            LocalDateTime.now(), null);

    @Test
    void testGetItemRequestById() throws ValidationException, ObjectNotFountException {
        userService.createUser(mockUser1);
        itemRequestService.createItemRequest(mockUser1.getId(), mockItemRequest);

        ItemRequest gottenItemRequest = itemRequestService.getItemRequestById(mockUser1.getId(), mockItemRequest.getId());

        assertThat(gottenItemRequest.getId(), equalTo(mockItemRequest.getId()));
        assertThat(gottenItemRequest.getDescription(), equalTo(mockItemRequest.getDescription()));
        assertThat(gottenItemRequest.getCreated(), equalTo(mockItemRequest.getCreated()));
    }

    @Test
    void testGetItemRequestByWrongId() {
        userService.createUser(mockUser1);

        Exception exception = assertThrows(ObjectNotFountException.class, () ->
                itemRequestService.getItemRequestById(mockUser1.getId(), mockItemRequest.getId()));

        assertEquals("Запроса вещи с id 1 не существует", exception.getMessage());
    }
}
