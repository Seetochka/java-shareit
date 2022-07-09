package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * Маппер запроса вещи
 */
public class ItemRequestMapper {
    /**
     * Преобразование модели в DTO
     */
    public static ItemRequestDto toItemDto(ItemRequest item) {
        return ItemRequestDto.builder()
                .id(item.getId())
                .description(item.getDescription())
                .requestor(toUserItemRequest(item.getRequestor()))
                .created(item.getCreated())
                .build();
    }

    /**
     * Преобразование DTO в модель
     */
    public static ItemRequest toItem(ItemRequestDto itemDto) {
        return ItemRequest.builder()
                .id(itemDto.getId())
                .description(itemDto.getDescription())
                .requestor(toUser(itemDto.getRequestor()))
                .created(itemDto.getCreated())
                .build();
    }

    private static ItemRequestDto.User toUserItemRequest(User user) {
        return ItemRequestDto.User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    private static User toUser(ItemRequestDto.User bookingUser) {
        return User.builder()
                .id(bookingUser.getId())
                .name(bookingUser.getName())
                .email(bookingUser.getEmail())
                .build();
    }
}
