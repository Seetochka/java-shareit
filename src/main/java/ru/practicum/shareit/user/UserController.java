package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.Collection;

/**
 * Контроллер отвечающий за действия с пользователем
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) throws DuplicateEmailException, ValidationException {
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable int userId) throws ObjectNotFountException {
        return userService.getUserById(userId);
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.getAll();
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable int userId, @Valid @RequestBody UserDto userDto)
            throws ObjectNotFountException, ValidationException, DuplicateEmailException {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public int deleteUser(@PathVariable int userId) throws ObjectNotFountException {
        return userService.deleteUser(userId);
    }
}
