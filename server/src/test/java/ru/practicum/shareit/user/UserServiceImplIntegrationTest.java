package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplIntegrationTest {
    private final UserService userService;
    User mockUser = new User(1L, "User", "1@user.com");

    @Test
    void testGetUserById() throws ObjectNotFountException {
        userService.createUser(mockUser);

        User gottenUser = userService.getUserById(1L);

        assertThat(gottenUser.getId(), equalTo(mockUser.getId()));
        assertThat(gottenUser.getName(), equalTo(mockUser.getName()));
        assertThat(gottenUser.getEmail(), equalTo(mockUser.getEmail()));
    }

    @Test
    void testGetUserByWrongId() {
        Exception exception = assertThrows(ObjectNotFountException.class, () -> userService.getUserById(1L));

        assertEquals("Пользователь с id 1 не существует", exception.getMessage());
    }
}
