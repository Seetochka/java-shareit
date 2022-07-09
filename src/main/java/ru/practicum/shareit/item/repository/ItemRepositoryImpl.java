package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private static int globalId = 1;

    private final Map<Integer, Item> items = new HashMap<>();

    /**
     * Создание вещи
     */
    @Override
    public Item createItem(int userId, Item item, User user) {
        item.setId(getNextId());
        item.setOwner(user);

        items.put(item.getId(), item);

        return item;
    }

    /**
     * Получение вещи по id
     */
    @Override
    public Item getItemById(int itemId) {
        return items.get(itemId);
    }

    /**
     * Получение всех вещей пользователя
     */
    @Override
    public Collection<Item> getAllByUserId(int userId) {
        return getAll()
                .values()
                .stream()
                .filter(i -> i.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    /**
     * Получение всех вещей
     */
    @Override
    public Map<Integer, Item> getAll() {
        return items;
    }

    /**
     * Обновление данных вещи
     */
    @Override
    public Item updateItem(int itemId, Item item) {
        Item itemUpdated = getItemById(itemId);

        if (item.getName() != null) {
            itemUpdated.setName(item.getName());
        }

        if (item.getDescription() != null) {
            itemUpdated.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            itemUpdated.setAvailable(item.getAvailable());
        }

        return itemUpdated;
    }

    /**
     * Удаление вещи
     */
    @Override
    public int deleteItem(int itemId) {
        Item item = items.remove(itemId);

        return item.getId();
    }

    /**
     * Поиск вещей по тексту
     */
    @Override
    public Collection<Item> searchItemByText(String text) {
        return getAll()
                .values()
                .stream()
                .filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase())
                        || i.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && i.getAvailable())
                .collect(Collectors.toList());
    }

    /**
     * Проверяет владельца вещи
     */
    @Override
    public boolean checkOwner(int userId, int itemId) {
        return items.get(itemId).getOwner().getId() != userId;
    }

    private static int getNextId() {
        return globalId++;
    }
}
