package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;

    private final ItemRepository itemRepository;

    /**
     * Создание вещи
     */
    @Override
    public ItemDto createItem(int userId, ItemDto itemDto) throws ObjectNotFountException, ValidationException {
        userService.checkUserId(userId);

        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Не заполнено поле name", "CreateItem");
        }

        if (itemDto.getDescription() == null) {
            throw new ValidationException("Не заполнено поле description", "CreateItem");
        }

        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Не заполнено поле available", "CreateItem");
        }

        UserDto userDto = userService.getUserById(userId);

        Item item = itemRepository.createItem(userId, ItemMapper.toItem(itemDto), UserMapper.toUser(userDto));

        log.info("CreateItem. Создана вещь с id {}", item.getId());
        return ItemMapper.toItemDto(item);
    }

    /**
     * Получение вещи по id
     */
    @Override
    public ItemDto getItemById(int itemId) throws ObjectNotFountException {
        checkItemId(itemId);

        Item item = itemRepository.getItemById(itemId);

        return ItemMapper.toItemDto(item);
    }

    /**
     * Получение всех вещей пользователя
     */
    @Override
    public Collection<ItemDto> getAllByUserId(int userId) throws ObjectNotFountException {
        userService.checkUserId(userId);

        return itemRepository.getAllByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    /**
     * Обновление данных вещи
     */
    @Override
    public ItemDto updateItem(int userId, int itemId, ItemDto itemDto)
            throws ObjectNotFountException {
        userService.checkUserId(userId);
        checkItemId(itemId);

        if (itemRepository.checkOwner(userId, itemId)) {
            throw new ObjectNotFountException("Передан неверный владелец вещи", "UpdateItem");
        }

        Item item = itemRepository.updateItem(itemId, ItemMapper.toItem(itemDto));

        log.info("UpdateItem. Обновлены данные вещи с id {}", item.getId());
        return ItemMapper.toItemDto(item);
    }

    /**
     * Удаление вещи
     */
    @Override
    public int deleteItem(int userId, int itemId) throws ObjectNotFountException {
        userService.checkUserId(userId);
        checkItemId(itemId);

        int itemDeletedId = itemRepository.deleteItem(itemId);

        log.info("DeleteItem. Удалена вещь с id {}", itemDeletedId);
        return itemDeletedId;
    }

    /**
     * Поиск вещей по тексту
     */
    @Override
    public Collection<ItemDto> searchItemByText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchItemByText(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkItemId(int itemId) throws ObjectNotFountException {
        if (!itemRepository.getAll().containsKey(itemId)) {
            throw new ObjectNotFountException(String.format("Вещь с id %d не существует", itemId), "CheckItemId");
        }
    }
}
