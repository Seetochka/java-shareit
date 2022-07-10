package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
    private final UserMapper userMapper;

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    /**
     * Создание вещи
     */
    @Override
    public ItemDto createItem(int userId, ItemDto itemDto) throws ObjectNotFountException, ValidationException {
        userService.checkUserId(userId);

        if (!StringUtils.hasText(itemDto.getName())) {
            throw new ValidationException("Не заполнено поле name", "CreateItem");
        }

        if (itemDto.getDescription() == null) {
            throw new ValidationException("Не заполнено поле description", "CreateItem");
        }

        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Не заполнено поле available", "CreateItem");
        }

        UserDto userDto = userService.getUserById(userId);

        Item item = itemRepository.createItem(userId, itemMapper.toItem(itemDto), userMapper.toUser(userDto));

        log.info("CreateItem. Создана вещь с id {}", item.getId());
        return itemMapper.toItemDto(item);
    }

    /**
     * Получение вещи по id
     */
    @Override
    public ItemDto getItemById(int itemId) throws ObjectNotFountException {
        itemRepository.checkItemId(itemId);

        Item item = itemRepository.getItemById(itemId);

        return itemMapper.toItemDto(item);
    }

    /**
     * Получение всех вещей пользователя
     */
    @Override
    public Collection<ItemDto> getAllByUserId(int userId) throws ObjectNotFountException {
        userService.checkUserId(userId);

        return itemRepository.getAllByUserId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    /**
     * Обновление данных вещи
     */
    @Override
    public ItemDto updateItem(int userId, int itemId, ItemDto itemDto)
            throws ObjectNotFountException {
        userService.checkUserId(userId);
        itemRepository.checkItemId(itemId);

        if (itemRepository.checkOwner(userId, itemId)) {
            throw new ObjectNotFountException("Передан неверный владелец вещи", "UpdateItem");
        }

        Item item = itemRepository.updateItem(itemId, itemMapper.toItem(itemDto));

        log.info("UpdateItem. Обновлены данные вещи с id {}", item.getId());
        return itemMapper.toItemDto(item);
    }

    /**
     * Удаление вещи
     */
    @Override
    public int deleteItem(int userId, int itemId) throws ObjectNotFountException {
        userService.checkUserId(userId);
        itemRepository.checkItemId(itemId);

        int itemDeletedId = itemRepository.deleteItem(itemId);

        log.info("DeleteItem. Удалена вещь с id {}", itemDeletedId);
        return itemDeletedId;
    }

    /**
     * Поиск вещей по тексту
     */
    @Override
    public Collection<ItemDto> searchItemByText(String text) {
        if (!StringUtils.hasText(text)) {
            return Collections.emptyList();
        }

        return itemRepository.searchItemByText(text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
