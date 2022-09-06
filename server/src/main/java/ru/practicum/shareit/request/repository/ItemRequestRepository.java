package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.List;

/**
 * Репозиторий запроса вещи
 */
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Collection<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(long userId);

    List<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(long userId, Pageable page);
}
