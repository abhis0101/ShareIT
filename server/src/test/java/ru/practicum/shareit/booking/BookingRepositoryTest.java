package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User user;
    private User user1;
    private Item item;
    private Booking booking;

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

        booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(20))
                .item(item)
                .booker(user1)
                .status(WAITING)
                .build();
    }


    @Test
    void findAllByBookerIdOrderByStartDesc() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user1);
        bookingRepository.save(booking);

        assertThat((long) bookingRepository.findAllByBookerIdOrderByStartDesc(user1.getId(),
                Pageable.ofSize(10)).size(), equalTo(1L));
    }

    @Test
    void findAllByBookerIdAndEndIsBeforeOrderByStartDesc() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user1);
        bookingRepository.save(booking);

        assertThat(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(user1.getId(),
                LocalDateTime.now().plusDays(30), Pageable.ofSize(10)).size(), equalTo(1));
    }

    @Test
    void findAllByBookerIdAndStartIsAfterOrderByStartDesc() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user1);
        bookingRepository.save(booking);

        assertThat(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(user1.getId(),
                LocalDateTime.now().minusDays(10), Pageable.ofSize(10)).size(), equalTo(1));
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user1);
        bookingRepository.save(booking);

        assertThat(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(user1.getId(),
                LocalDateTime.now().plusDays(30), LocalDateTime.now(), Pageable.ofSize(10)).size(), equalTo(1));
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user1);
        booking.setStatus(APPROVED);
        bookingRepository.save(booking);

        assertThat(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(user1.getId(),
                APPROVED, Pageable.ofSize(10)).size(), equalTo(1));
    }

    @Test
    void findTopByItemIdAndStatusAndStartIsAfterOrderByStart() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user1);

        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setStatus(APPROVED);
        bookingRepository.save(booking);

        assertThat(bookingRepository.findTopByItemIdAndStatusAndStartIsAfterOrderByStart(item.getId(),
                APPROVED, LocalDateTime.now()), equalTo(Optional.of(booking)));
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDesc() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user1);
        bookingRepository.save(booking);

        assertThat(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(user.getId(),
                Pageable.ofSize(10)).size(), equalTo(1));
    }

    @Test
    void findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user1);
        bookingRepository.save(booking);

        assertThat(bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(user.getId(),
                LocalDateTime.now().plusDays(30), Pageable.ofSize(10)).size(), equalTo(1));
    }

    @Test
    void findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user1);
        bookingRepository.save(booking);

        assertThat(bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(user.getId(),
                LocalDateTime.now().minusDays(10), Pageable.ofSize(10)).size(), equalTo(1));
    }

    @Test
    void findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user1);
        bookingRepository.save(booking);

        assertThat(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(user.getId(),
                LocalDateTime.now().plusDays(30), LocalDateTime.now(), Pageable.ofSize(10)).size(), equalTo(1));
    }

    @Test
    void findAllByItemOwnerIdAndStatusOrderByStartDesc() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user1);
        booking.setStatus(APPROVED);
        bookingRepository.save(booking);

        assertThat(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(user.getId(),
                APPROVED, Pageable.ofSize(10)).size(), equalTo(1));
    }

    @Test
    void findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user1);
        booking.setStatus(APPROVED);
        bookingRepository.save(booking);

        assertThat(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(user1.getId(),
                        item.getId(), APPROVED, LocalDateTime.now().plusDays(30)).size(),
                equalTo(1));
    }

}
