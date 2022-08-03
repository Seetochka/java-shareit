package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

/**
 * Сервис вещей
 */
public interface ItemService {
    /**
     * Создание вещи
     */
    Item createItem(long userId, Item item) throws ObjectNotFountException, ValidationException;

    /**
     * Получение вещи по id
     */
    Item getItemById(long userId, long itemId) throws ObjectNotFountException;

    /**
     * Получение всех вещей пользователя
     */
    Collection<Item> getAllByUserId(long userId, int from, int size) throws ObjectNotFountException;

    /**
     * Обновление данных вещи
     */
    Item updateItem(long userId, long itemId, Item item) throws ObjectNotFountException;

    /**
     * Удаление вещи
     */
    void deleteItem(long userId, long itemId) throws ObjectNotFountException;

    /**
     * Поиск вещей по тексту
     */
    Collection<Item> searchItemByText(String text, int from, int size);

    /**
     * Создание отзыва
     */
    Comment createComment(long userId, long itemId, Comment comment)
            throws ObjectNotFountException, ValidationException;

    /**
     * Проверка существования вещи по id
     */
    void checkItemExistsById(long itemId) throws ObjectNotFountException;
}
