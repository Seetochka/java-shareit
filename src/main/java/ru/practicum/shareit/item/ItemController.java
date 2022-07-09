package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
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
    public ItemDto createItem(@RequestHeader(HEADER_USER_ID) int userId, @Valid @RequestBody ItemDto itemDto)
            throws ObjectNotFountException, ValidationException {
        return itemService.createItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable int itemId) throws ObjectNotFountException {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAll(@RequestHeader(HEADER_USER_ID) int userId) throws ObjectNotFountException {
        return itemService.getAllByUserId(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER_USER_ID) int userId, @PathVariable int itemId,
                              @Valid @RequestBody ItemDto itemDto) throws ObjectNotFountException {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public int deleteItem(@RequestHeader(HEADER_USER_ID) int userId, @PathVariable int itemId)
            throws ObjectNotFountException {
        return itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItemByText(@RequestParam String text) {
        return itemService.searchItemByText(text);
    }
}
