package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Контроллер отвечающий за действия с вещами
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @PostMapping
    public ItemDto createItem(@RequestHeader(HEADER_USER_ID) long userId, @Valid @RequestBody ItemDto itemDto)
            throws ObjectNotFountException, ValidationException {
        Item item = itemService.createItem(userId, itemMapper.toItem(itemDto));

        return itemMapper.toItemDto(item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long itemId)
            throws ObjectNotFountException {
        return itemMapper.toItemDto((itemService.getItemById(userId, itemId)));
    }

    @GetMapping
    public Collection<ItemDto> getAllByUserId(@RequestHeader(HEADER_USER_ID) long userId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "20") int size)
            throws ObjectNotFountException {
        return itemService.getAllByUserId(userId, from, size)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) throws ObjectNotFountException {
        Item item = itemService.updateItem(userId, itemId, itemMapper.toItem(itemDto));

        return itemMapper.toItemDto(item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long itemId)
            throws ObjectNotFountException {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItemByText(@RequestParam String text,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "20") int size) {
        return itemService.searchItemByText(text, from, size)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long itemId,
                                    @Valid @RequestBody CommentDto commentDto)
            throws ObjectNotFountException, ValidationException {
        Comment comment = itemService.createComment(userId, itemId, commentMapper.toComment(commentDto));

        return commentMapper.toCommentDto(comment);
    }
}
