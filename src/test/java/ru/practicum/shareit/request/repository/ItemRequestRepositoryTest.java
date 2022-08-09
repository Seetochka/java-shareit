package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {
    private static final int PAGE = 0;
    private static final int SIZE = 20;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private static final User mockUser1 = new User(1L, "User1", "1@user.com");
    private static final User mockUser2 = new User(2L, "User2", "2@user.com");
    private static final ItemRequest mockItemRequest1 = new ItemRequest(1L, "ItemRequestDesc1", mockUser1,
            LocalDateTime.now().plusDays(1), null);
    private static final ItemRequest mockItemRequest2 = new ItemRequest(2L, "ItemRequestDesc2", mockUser2,
            LocalDateTime.now().plusDays(1), null);

    @Test
    void testFindAllByRequestorIdOrderByCreatedDesc() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRequestRepository.save(mockItemRequest1);
        itemRequestRepository.save(mockItemRequest2);

        Collection<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(1L);

        assertThat(itemRequests).isNotEmpty();
        assertThat(itemRequests).hasSize(1).contains(mockItemRequest1);
    }

    @Test
    void testFindAllByRequestorIdNotOrderByCreatedDesc() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRequestRepository.save(mockItemRequest1);
        itemRequestRepository.save(mockItemRequest2);

        Sort sortById = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(PAGE, SIZE, sortById);

        Collection<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(1L,page);

        assertThat(itemRequests).isNotEmpty();
        assertThat(itemRequests).hasSize(1).contains(mockItemRequest2);
    }
}