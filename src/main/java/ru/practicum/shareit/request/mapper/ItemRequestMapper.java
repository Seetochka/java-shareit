package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * Маппер запроса вещи
 */
@Component
public class ItemRequestMapper {
    /**
     * Преобразование модели в DTO
     */
    public ItemRequestDto toItemDto(ItemRequest item) {
        return new ItemRequestDto(
                item.getId(),
                item.getDescription(),
                toUserItemRequest(item.getRequestor()),
                item.getCreated()
        );
    }

    /**
     * Преобразование DTO в модель
     */
    public ItemRequest toItem(ItemRequestDto itemDto) {
        return new ItemRequest(
                itemDto.getId(),
                itemDto.getDescription(),
                toUser(itemDto.getRequestor()),
                itemDto.getCreated()
        );
    }

    private ItemRequestDto.User toUserItemRequest(User user) {
        return new ItemRequestDto.User(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    private User toUser(ItemRequestDto.User bookingUser) {
        return new User(
                bookingUser.getId(),
                bookingUser.getName(),
                bookingUser.getEmail()
        );
    }
}
