package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Контроллер отвечающий за действия с запросами вещей
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;
    private final ItemRequestMapper itemRequestMapper;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(HEADER_USER_ID) long userId,
                                            @Valid @RequestBody ItemRequestDto itemRequestDto)
            throws ObjectNotFountException, ValidationException {
        ItemRequest itemRequest = itemRequestService.createItemRequest(userId,
                itemRequestMapper.toItemRequest(itemRequestDto));

        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    @GetMapping
    public Collection<ItemRequestDto> getItemRequestsByUserId(@RequestHeader(HEADER_USER_ID) long userId)
            throws ObjectNotFountException {
        return itemRequestService.getItemRequestsByUser(userId)
                .stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllItemRequest(@RequestHeader(HEADER_USER_ID) long userId,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "20") int size)
            throws ObjectNotFountException, InvalidParameterException {
        return itemRequestService.getAllItemRequest(userId, from, size)
                .stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long requestId)
            throws ObjectNotFountException {
        return itemRequestMapper.toItemRequestDto(itemRequestService.getItemRequestById(userId, requestId));
    }
}
