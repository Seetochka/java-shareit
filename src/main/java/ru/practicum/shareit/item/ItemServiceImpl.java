package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;

    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;

    /**
     * Создание вещи
     */
    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) throws ObjectNotFountException, ValidationException {
        userService.getUserById(userId);

        if (!StringUtils.hasText(itemDto.getName())) {
            throw new ValidationException("Не заполнено поле name", "CreateItem");
        }

        if (itemDto.getDescription() == null) {
            throw new ValidationException("Не заполнено поле description", "CreateItem");
        }

        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Не заполнено поле available", "CreateItem");
        }

        itemDto.setOwner(new ItemDto.User(userId, null, null));
        Item itemCreated = itemRepository.save(itemMapper.toItem(itemDto));

        log.info("CreateItem. Создана вещь с id {}", itemCreated.getId());
        return itemMapper.toItemDto(itemCreated);
    }

    /**
     * Получение вещи по id
     */
    @Override
    public ItemDto getItemById(long userId, long itemId) throws ObjectNotFountException {
        userService.getUserById(userId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFountException(
                String.format("Вещь с id %d не существует", itemId),
                "getItemById"
        ));

        ItemDto itemDto = itemMapper.toItemDto(item);

        if (item.getOwner().getId() == userId) {
            setBookings(itemDto);
        }

        return itemDto;
    }

    /**
     * Получение всех вещей пользователя
     */
    @Override
    public Collection<ItemDto> getAllByUserId(long userId) throws ObjectNotFountException {
        userService.getUserById(userId);

        return itemRepository.findAllByOwnerId(userId).stream()
                .map(itemMapper::toItemDto)
                .map(this::setBookings)
                .collect(Collectors.toList());
    }

    /**
     * Обновление данных вещи
     */
    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto)
            throws ObjectNotFountException {
        userService.getUserById(userId);
        Item itemUpdated = itemMapper.toItem(getItemById(userId, itemId));

        if (itemUpdated.getOwner().getId() != userId) {
            throw new ObjectNotFountException("Передан неверный владелец вещи", "UpdateItem");
        }

        if (itemDto.getName() != null) {
            itemUpdated.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            itemUpdated.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            itemUpdated.setAvailable(itemDto.getAvailable());
        }

        log.info("UpdateItem. Обновлены данные вещи с id {}", itemUpdated.getId());
        return itemMapper.toItemDto(itemRepository.save(itemUpdated));
    }

    /**
     * Удаление вещи
     */
    @Override
    public void deleteItem(long userId, long itemId) throws ObjectNotFountException {
        userService.getUserById(userId);
        itemRepository.findById(itemId);

        itemRepository.deleteById(itemId);

        log.info("DeleteItem. Удалена вещь с id {}", itemId);
    }

    /**
     * Поиск вещей по тексту
     */
    @Override
    public Collection<ItemDto> searchItemByText(String text) {
        if (!StringUtils.hasText(text)) {
            return Collections.emptyList();
        }

        return itemRepository.search(text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    /**
     * Создание отзыва
     */
    @Override
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto)
            throws ObjectNotFountException, ValidationException {
        User user = userMapper.toUser(userService.getUserById(userId));
        Item item = itemMapper.toItem(getItemById(userId, itemId));

        bookingRepository.findFirstByBookerIdAndItemIdAndStatusAndStartBefore(userId, itemId, BookingStatus.APPROVED,
                        LocalDateTime.now())
                .orElseThrow(() -> new ValidationException(
                        String.format("Пользователь с id %d не брал вещь с id %d в аренду", userId, itemId),
                        "GetBookingById"
                ));

        Comment commentCreated = commentMapper.toComment(commentDto);
        commentCreated.setAuthor(user);
        commentCreated.setItem(item);
        commentCreated.setCreated(LocalDateTime.now());

        log.info("CreateComment. Создан отзыв с id {}", commentCreated.getId());
        return commentMapper.toCommentDto(commentRepository.save(commentCreated));
    }

    private ItemDto setBookings(ItemDto itemDto) {
        Optional<ItemDto.Booking> last = getLastBookingForItem(itemDto.getOwner().getId());
        Optional<ItemDto.Booking> next = getNextBookingForItem(itemDto.getOwner().getId());

        itemDto.setLastBooking(last.orElse(null));
        itemDto.setNextBooking(next.orElse(null));

        return itemDto;
    }

    /**
     * Получедние последнего бронирования для вещи
     */
    private Optional<ItemDto.Booking> getLastBookingForItem(long userId) {
        Optional<Booking> last = bookingRepository.findFirstByItemOwnerIdAndStatusOrderByEnd(userId,
                BookingStatus.APPROVED);

        if (last.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(itemMapper.toItemBookingDto(last.get()));
    }

    /**
     * Получедние ближайшего слудующего бронирования для вещи
     */
    private Optional<ItemDto.Booking> getNextBookingForItem(long userId) {
        Optional<Booking> next = bookingRepository.findFirstByItemOwnerIdAndStatusOrderByEndDesc(userId,
                BookingStatus.APPROVED);

        if (next.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(itemMapper.toItemBookingDto(next.get()));
    }
}
