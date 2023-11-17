package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;


    private User user;
    private User user1;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void init() {

        user = User.builder()
                .name("Oleg")
                .email("oleg@email.com")
                .build();

        user1 = User.builder()
                .name("Ivan")
                .email("ivan@email.com")
                .build();

        item = Item.builder()
                .name("Hammer")
                .description("hammering the nail")
                .available(true)
                .owner(user)
                .build();

        itemRequest = ItemRequest
                .builder()
                .description("request description")
                .requester(user)
                .created(LocalDateTime.now())
                .build();


    }

    @Test
    void searchItemByText() {
        userRepository.save(user);
        itemRepository.save(item);

        Page<Item> items = itemRepository.search("Hammer", Pageable.ofSize(10));

        assertThat(items.stream().count(), equalTo(1L));
    }

    @Test
    void findAllByItemOwnerId() {
        userRepository.save(user);
        itemRepository.save(item);

        Page<Item> items = itemRepository.findAllByOwnerIdOrderById(user.getId(), Pageable.ofSize(10));

        assertThat(items.stream().count(), equalTo(1L));
    }

    @Test
    void findAllByRequestId() {
        userRepository.save(user);
        itemRequest.setRequester(user);
        itemRequestRepository.save(itemRequest);

        item.setRequest(itemRequest);
        itemRepository.save(item);

        List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(item.getId()));
    }

    @Test
    void findAllByRequestIn() {
        userRepository.save(user);
        itemRequest.setRequester(user);
        itemRequestRepository.save(itemRequest);

        item.setRequest(itemRequest);
        itemRepository.save(item);

        List<ItemRequest> requests = new ArrayList<>();
        requests.add(itemRequest);

        List<Item> items = itemRepository.findAllByRequestIn(requests);

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(item.getId()));
    }


}
