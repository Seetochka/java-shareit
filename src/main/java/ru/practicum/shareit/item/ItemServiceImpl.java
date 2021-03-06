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
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserService;
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

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;

    /**
     * Создание вещи
     */
    @Override
    public Item createItem(long userId, Item item) throws ObjectNotFountException, ValidationException {
        User user = userService.getUserById(userId);

        if (!StringUtils.hasText(item.getName())) {
            throw new ValidationException("Не заполнено поле name", "CreateItem");
        }

        if (item.getDescription() == null) {
            throw new ValidationException("Не заполнено поле description", "CreateItem");
        }

        if (item.getAvailable() == null) {
            throw new ValidationException("Не заполнено поле available", "CreateItem");
        }

        item.setOwner(user);

        Item itemCreated = itemRepository.save(item);

        log.info("CreateItem. Создана вещь с id {}", itemCreated.getId());
        return itemCreated;
    }

    /**
     * Получение вещи по id
     */
    @Override
    public Item getItemById(long userId, long itemId) throws ObjectNotFountException {
        userService.checkUserExistsById(userId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFountException(
                String.format("Вещь с id %d не существует", itemId),
                "getItemById"
        ));

        if (item.getOwner().getId() == userId) {
            setBookings(item);
        }

        return item;
    }

    /**
     * Получение всех вещей пользователя
     */
    @Override
    public Collection<Item> getAllByUserId(long userId) throws ObjectNotFountException {
        userService.checkUserExistsById(userId);

        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(this::setBookings)
                .collect(Collectors.toList());
    }

    /**
     * Обновление данных вещи
     */
    @Override
    public Item updateItem(long userId, long itemId, Item item) throws ObjectNotFountException {
        userService.checkUserExistsById(userId);
        Item itemUpdated = getItemById(userId, itemId);

        if (itemUpdated.getOwner().getId() != userId) {
            throw new ObjectNotFountException("Передан неверный владелец вещи", "UpdateItem");
        }

        Optional.ofNullable(item.getName()).ifPresent(itemUpdated::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(itemUpdated::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(itemUpdated::setAvailable);

        log.info("UpdateItem. Обновлены данные вещи с id {}", itemUpdated.getId());
        return itemRepository.save(itemUpdated);
    }

    /**
     * Удаление вещи
     */
    @Override
    public void deleteItem(long userId, long itemId) throws ObjectNotFountException {
        userService.checkUserExistsById(userId);
        checkItemExistsById(itemId);

        itemRepository.deleteById(itemId);

        log.info("DeleteItem. Удалена вещь с id {}", itemId);
    }

    /**
     * Поиск вещей по тексту
     */
    @Override
    public Collection<Item> searchItemByText(String text) {
        if (!StringUtils.hasText(text)) {
            return Collections.emptyList();
        }

        return itemRepository.search(text);
    }

    /**
     * Создание отзыва
     */
    @Override
    public Comment createComment(long userId, long itemId, Comment comment)
            throws ObjectNotFountException, ValidationException {
        User user = userService.getUserById(userId);
        Item item = getItemById(userId, itemId);

        bookingRepository.findFirstByBookerIdAndItemIdAndStatusAndStartBefore(userId, itemId, BookingStatus.APPROVED,
                        LocalDateTime.now())
                .orElseThrow(() -> new ValidationException(
                        String.format("Пользователь с id %d не брал вещь с id %d в аренду", userId, itemId),
                        "GetBookingById"
                ));

        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        log.info("CreateComment. Создан отзыв с id {}", comment.getId());
        return commentRepository.save(comment);
    }

    /**
     * Проверка существования пользователя по id
     */
    @Override
    public void checkItemExistsById(long itemId) throws ObjectNotFountException {
        if (!itemRepository.existsById(itemId)) {
            throw new ObjectNotFountException(
                    String.format("Вещь с id %d не существует", itemId),
                    "CheckItemExistsById"
            );
        }
    }

    private Item setBookings(Item item) {
        Optional<Booking> last = getLastBookingForItem(item.getOwner().getId());
        Optional<Booking> next = getNextBookingForItem(item.getOwner().getId());

        item.setLastBooking(last.orElse(null));
        item.setNextBooking(next.orElse(null));

        return item;
    }

    /**
     * Получедние последнего бронирования для вещи
     */
    private Optional<Booking> getLastBookingForItem(long userId) {
        return bookingRepository.findFirstByItemOwnerIdAndStatusOrderByEnd(userId,
                BookingStatus.APPROVED);
    }

    /**
     * Получедние ближайшего слудующего бронирования для вещи
     */
    private Optional<Booking> getNextBookingForItem(long userId) {
        return bookingRepository.findFirstByItemOwnerIdAndStatusOrderByEndDesc(userId,
                BookingStatus.APPROVED);
    }
}
