package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceImplIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;

    private final User mockUser1 = new User(1L, "User1", "1@user.com");

    private final Item mockItem1 = new Item(1L, "Item1", "ItemDesc1", true, mockUser1,
            null, null, null, null);

    @Test
    void testSearchItemByText() throws ValidationException, ObjectNotFountException {
        userService.createUser(mockUser1);
        itemService.createItem(mockUser1.getId(), mockItem1);

        Collection<Item> items = itemService.searchItemByText("Desc", 0, 20);

        assertThat(items, hasSize(1));
        assertThat(items.stream().findFirst().isPresent(), is(true));
        assertThat(items.stream().findFirst().get().getId(), equalTo(mockItem1.getId()));
        assertThat(items.stream().findFirst().get().getName(), equalTo(mockItem1.getName()));
        assertThat(items.stream().findFirst().get().getDescription(), equalTo(mockItem1.getDescription()));
    }

    @Test
    void testSearchItemByEmptyText() throws ValidationException, ObjectNotFountException {
        userService.createUser(mockUser1);
        itemService.createItem(mockUser1.getId(), mockItem1);

        Collection<Item> items = itemService.searchItemByText("", 0, 20);

        assertThat(items, hasSize(0));
        assertThat(items, empty());
    }
}
