package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.trait.PageTrait;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Сервис запроса вещи
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService, PageTrait {
    private final UserService userService;

    private final ItemRequestRepository itemRequestRepository;

    /**
     * Создание запроса вещи
     */
    @Override
    public ItemRequest createItemRequest(long userId, ItemRequest itemRequest)
            throws ObjectNotFountException, ValidationException {
        User user = userService.getUserById(userId);

        if (itemRequest.getDescription() == null) {
            throw new ValidationException("Не заполнено поле description", "CreateItem");
        }

        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest itemRequestCreated = itemRequestRepository.save(itemRequest);

        log.info("createItemRequest. Создан запрос вещи с id {}", itemRequestCreated.getId());
        return itemRequestCreated;
    }

    /**
     * Получение списка своих запросов
     */
    @Override
    public Collection<ItemRequest> getItemRequestsByUser(long userId) throws ObjectNotFountException {
        userService.checkUserExistsById(userId);

        return itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
    }

    /**
     * Получение списка запросов созданных другими пользователями
     */
    @Override
    public Collection<ItemRequest> getAllItemRequest(long userId, int from, int size)
            throws ObjectNotFountException {
        userService.checkUserExistsById(userId);

        Pageable page = getPage(from, size, "created", Sort.Direction.ASC);

        return itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId, page);
    }

    /**
     * Получение запроса вещи по id
     */
    @Override
    public ItemRequest getItemRequestById(long userId, long requestId) throws ObjectNotFountException {
        userService.checkUserExistsById(userId);

        return itemRequestRepository.findById(
                requestId).orElseThrow(() -> new ObjectNotFountException(
                        String.format("Запроса вещи с id %d не существует", requestId),
                        "GetItemRequestById"
                )
        );
    }

    /**
     * Проверка существования запроса вещи по id
     */
    @Override
    public void checkItemRequestExistsById(long requestId) throws ObjectNotFountException {
        if (!itemRequestRepository.existsById(requestId)) {
            throw new ObjectNotFountException(
                    String.format("Запроса вещи с id %d не существует", requestId),
                    "CheckUserExistsItemRequestById"
            );
        }
    }
}
