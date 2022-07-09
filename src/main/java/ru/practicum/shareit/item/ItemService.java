package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

/**
 * Сервис вещей
 */
public interface ItemService {
    /**
     * Создание вещи
     */
    ItemDto createItem(int userId, ItemDto itemDto) throws ObjectNotFountException, ValidationException;

    /**
     * Получение вещи по id
     */
    ItemDto getItemById(int itemId) throws ObjectNotFountException;

    /**
     * Получение всех вещей пользователя
     */
    Collection<ItemDto> getAllByUserId(int userId) throws ObjectNotFountException;

    /**
     * Обновление данных вещи
     */
    ItemDto updateItem(int userId, int itemId, ItemDto itemDto) throws ObjectNotFountException;

    /**
     * Удаление вещи
     */
    int deleteItem(int userId, int itemId) throws ObjectNotFountException;

    /**
     * Поиск вещей по тексту
     */
    Collection<ItemDto> searchItemByText(String text);
}
