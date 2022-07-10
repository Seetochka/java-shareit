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
    public ItemRequest toItem(ItemRequestDto itemDto) {
        return ItemRequest.builder()
                .id(itemDto.getId())
                .description(itemDto.getDescription())
                .requestor(toUser(itemDto.getRequestor()))
                .created(itemDto.getCreated())
                .build();
    }

    private ItemRequestDto.User toUserItemRequest(User user) {
        return ItemRequestDto.User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    private User toUser(ItemRequestDto.User bookingUser) {
        return User.builder()
                .id(bookingUser.getId())
                .name(bookingUser.getName())
                .email(bookingUser.getEmail())
                .build();
    }
}
