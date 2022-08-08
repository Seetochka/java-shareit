package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {
    private static final int PAGE = 0;
    private static final int SIZE = 20;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private final User mockUser1 = new User(1L, "User1", "1@user.com");
    private final User mockUser2 = new User(2L, "User2", "2@user.com");
    private final Item mockItem1 = new Item(1L, "Item1", "ItemDesc1", true, mockUser1, null, null, null, null);
    private final Item mockItem2 = new Item(null, "Item2", "ItemDesc2", true, mockUser2, null, null, null, null);

    @Test
    void testFindAllByOwnerId() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);

        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(PAGE, SIZE, sortById);

        Collection<Item> items = itemRepository.findAllByOwnerId(1L, page);

        assertThat(items).isNotEmpty();
        assertThat(items).hasSize(1).contains(mockItem1);
    }

    @Test
    void testSearch() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);

        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(PAGE, SIZE, sortById);

        Collection<Item> items = itemRepository.search("Desc2", page);

        assertThat(items).isNotEmpty();
        assertThat(items).hasSize(1).contains(mockItem2);
    }
}
