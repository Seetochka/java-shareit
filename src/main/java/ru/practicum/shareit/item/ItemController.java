package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.Collection;

/**
 * Контроллер отвечающий за действия с вещами
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(HEADER_USER_ID) long userId, @Valid @RequestBody ItemDto itemDto)
            throws ObjectNotFountException, ValidationException {
        return itemService.createItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long itemId)
            throws ObjectNotFountException {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllByUserId(@RequestHeader(HEADER_USER_ID) long userId)
            throws ObjectNotFountException {
        return itemService.getAllByUserId(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) throws ObjectNotFountException {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long itemId)
            throws ObjectNotFountException {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItemByText(@RequestParam String text) {
        return itemService.searchItemByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long itemId,
                                    @Valid @RequestBody CommentDto commentDto)
            throws ObjectNotFountException, ValidationException {
        return itemService.createComment(userId, itemId, commentDto);
    }
}
