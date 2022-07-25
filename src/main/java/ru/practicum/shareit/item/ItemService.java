package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

/**
 * Сервис вещей
 */
public interface ItemService {
    /**
     * Создание вещи
     */
    ItemDto createItem(long userId, ItemDto itemDto) throws ObjectNotFountException, ValidationException;

    /**
     * Получение вещи по id
     */
    ItemDto getItemById(long userId, long itemId) throws ObjectNotFountException;

    /**
     * Получение всех вещей пользователя
     */
    Collection<ItemDto> getAllByUserId(long userId) throws ObjectNotFountException;

    /**
     * Обновление данных вещи
     */
    ItemDto updateItem(long userId, long itemId, ItemDto itemDto) throws ObjectNotFountException;

    /**
     * Удаление вещи
     */
    void deleteItem(long userId, long itemId) throws ObjectNotFountException;

    /**
     * Поиск вещей по тексту
     */
    Collection<ItemDto> searchItemByText(String text);

    /**
     * Создание отзыва
     */
    CommentDto createComment(long userId, long itemId, CommentDto commentDto)
            throws ObjectNotFountException, ValidationException;
}
