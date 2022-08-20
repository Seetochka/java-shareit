package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplUnitTest {
    private final User mockUser1 = new User(1L, "User1", "1@user.com");
    private final User mockUpdatedUser1 = new User(1L, "User1Update", "1@user.com");
    private final User mockUser2 = new User(2L, "User2", "2@user.com");

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private MockitoSession session;

    @BeforeEach
    void setUp() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        userService = new UserServiceImpl(userRepository);
    }

    @AfterEach
    void tearDown() {
        session.finishMocking();
    }

    @Test
    void testCreateUser() {
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(mockUser1);

        User user = userService.createUser(mockUser1);

        Mockito.verify(userRepository, Mockito.times(1))
                .save(mockUser1);

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(mockUser1.getName()));
        assertThat(user.getEmail(), equalTo(mockUser1.getEmail()));
    }

    @Test
    void testGetUserById() throws ObjectNotFountException {
        Mockito.when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(mockUser1));

        User user = userService.getUserById(1L);

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(1L);

        assertThat(user.getName(), equalTo(mockUser1.getName()));
        assertThat(user.getEmail(), equalTo(mockUser1.getEmail()));
    }

    @Test
    void testGetUserByWrongId() {
        Mockito.when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ObjectNotFountException.class, () -> userService.getUserById(1L));

        assertEquals("Пользователь с id 1 не существует", exception.getMessage());
    }

    @Test
    void testGetAll() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(mockUser1, mockUser2));

        Collection<User> users = userService.getAll();

        Mockito.verify(userRepository, Mockito.times(1))
                .findAll();

        assertThat(users, hasSize(2));
        assertThat(users, equalTo(List.of(mockUser1, mockUser2)));
    }

    @Test
    void testUpdateUser() throws ObjectNotFountException {
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(mockUpdatedUser1);
        Mockito.when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(mockUser1));

        mockUser1.setName("User1Update");

        User user = userService.updateUser(mockUser1.getId(), mockUser1);

        Mockito.verify(userRepository, Mockito.times(1))
                .save(mockUser1);

        assertThat(user.getId(), equalTo(mockUpdatedUser1.getId()));
        assertThat(user.getName(), equalTo(mockUpdatedUser1.getName()));
    }

    @Test
    void testDeleteUser() throws ObjectNotFountException {
        Mockito.when(userRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(true);

        userService.deleteUser(1L);

        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(1L);
    }

    @Test
    void testCheckUserExistsById() throws ObjectNotFountException {
        Mockito.when(userRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(true);

        userService.checkUserExistsById(1L);

        Mockito.verify(userRepository, Mockito.times(1))
                .existsById(1L);
    }

    @Test
    void testCheckUserNotExistsById() {
        Mockito.when(userRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(false);

        Exception exception = assertThrows(ObjectNotFountException.class, () -> userService.checkUserExistsById(1L));

        assertEquals("Пользователь с id 1 не существует", exception.getMessage());
    }
}
