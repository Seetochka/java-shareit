package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    private UserService userService;
    @MockBean
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final UserDto mockUserDto = new UserDto(1L, "User1", "1@user.com");
    private final User mockUser = new User(1L, "User1", "1@user.com");

    @Test
    void testCreateUser() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(mockUser);
        doReturn(mockUserDto).when(userMapper).toUserDto(any());

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(mockUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(mockUserDto.getName())))
                .andExpect(jsonPath("$.email", is(mockUserDto.getEmail())));
    }

    @Test
    void testGetUserById() throws Exception {
        when(userService.getUserById(any(Long.class)))
                .thenReturn(mockUser);
        doReturn(mockUserDto).when(userMapper).toUserDto(any());

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(mockUserDto.getName())))
                .andExpect(jsonPath("$.email", is(mockUserDto.getEmail())));
    }

    @Test
    void testGetAll() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(mockUser));
        doReturn(mockUserDto).when(userMapper).toUserDto(any());

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(mockUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(mockUserDto.getName())))
                .andExpect(jsonPath("$.[0].email", is(mockUserDto.getEmail())));
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.updateUser(any(Long.class), any()))
                .thenReturn(mockUser);
        doReturn(mockUserDto).when(userMapper).toUserDto(any());

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(mockUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(mockUserDto.getName())))
                .andExpect(jsonPath("$.email", is(mockUserDto.getEmail())));
    }

    @Test
    void testDeleteUser() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}
