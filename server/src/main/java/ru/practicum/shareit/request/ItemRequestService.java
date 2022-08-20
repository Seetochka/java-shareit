package ru.practicum.shareit.request;

import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

/**
 * Интерфейс сервиса запроса вещи
 */
public interface ItemRequestService {
    /**
     * Создание запроса вещи
     */
    ItemRequest createItemRequest(long userId, ItemRequest toItem) throws ObjectNotFountException, ValidationException;

    /**
     * Получение списка своих запросов
     */
    Collection<ItemRequest> getItemRequestsByUser(long userId) throws ObjectNotFountException;

    /**
     * Получение списка запросов созданных другими пользователями
     */
    Collection<ItemRequest> getAllItemRequest(long userId, int from, int size) throws ObjectNotFountException;

    /**
     * Получение запроса вещи по id
     */
    ItemRequest getItemRequestById(long userId, long requestId) throws ObjectNotFountException;

    /**
     * Проверка существования запроса вещи по id
     */
    void checkItemRequestExistsById(long requestId) throws ObjectNotFountException;
}
