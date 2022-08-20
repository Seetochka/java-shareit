package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Контроллер отвечающий за действия с пользователем
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        User user = userService.createUser(userMapper.toUser(userDto));

        return userMapper.toUserDto(user);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) throws ObjectNotFountException {
        return userMapper.toUserDto((userService.getUserById(userId)));
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.getAll()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto)
            throws ObjectNotFountException {
        User user = userService.updateUser(userId, userMapper.toUser(userDto));

        return userMapper.toUserDto(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) throws ObjectNotFountException {
        userService.deleteUser(userId);
    }
}
