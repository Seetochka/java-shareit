package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Map;

/**
 * Интерфейс репозитория вещи
 */
public interface ItemRepository {
    /**
     * Создание вещи
     */
    Item createItem(int userId, Item item, User user);

    /**
     * Получение вещи по id
     */
    Item getItemById(int itemId);

    /**
     * Получение всех вещей пользователя
     */
    Collection<Item> getAllByUserId(int userId);

    /**
     * Получение всех вещей
     */
    Map<Integer, Item> getAll();

    /**
     * Обновление данных вещи
     */
    Item updateItem(int itemId, Item item);

    /**
     * Удаление вещи
     */
    int deleteItem(int itemId);

    /**
     * Поиск вещей по тексту
     */
    Collection<Item> searchItemByText(String text);

    /**
     * Проверяет владельца вещи
     */
    boolean checkOwner(int userId, int itemId);

    /**
     * Проверяет существование вещи
     */
    void checkItemId(int itemId) throws ObjectNotFountException;
}
