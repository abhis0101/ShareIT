package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.ModelNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceImplTest {

    private final EntityManager em;
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final LocalDateTime timestamp1 = LocalDateTime.of(2022, 11, 20, 10, 30);
    private final LocalDateTime timestamp2 = LocalDateTime.of(2022, 11, 22, 11, 30);
    private BookingRequestDto bookingRequestDto;


    @BeforeEach
    void init() {

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Oleg")
                .email("oleg@email.com")
                .build();
        userService.createUser(userDto);

        UserDto userDto1 = UserDto.builder()
                .id(2L)
                .name("Roman")
                .email("roman@email.com")
                .build();
        userService.createUser(userDto1);

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();
        itemService.createItem(userDto.getId(), itemDto);

        bookingRequestDto = BookingRequestDto
                .builder()
                .start(timestamp1)
                .end(timestamp2)
                .itemId(1L)
                .build();
    }

    @Test
    void createBooking() {

        bookingService.createBooking(2L, bookingRequestDto);

        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.id = :id", Booking.class);
        Booking booking = query
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(booking.getStart(), equalTo(timestamp1));
        assertThat(booking.getEnd(), equalTo(timestamp2));
        assertThat(booking.getStatus(), equalTo(WAITING));
    }

    @Test
    void failCreatingBookingWithSameOwner() {
        ModelNotFoundException e = assertThrows(ModelNotFoundException.class,
                () -> bookingService.createBooking(1L, bookingRequestDto));
        assertThat(e.getMessage(), equalTo("Item cannot be booked"));
    }

    @Test
    void failCreatingBookingWithWrongUserId() {
        ModelNotFoundException e = assertThrows(ModelNotFoundException.class,
                () -> bookingService.createBooking(50L, bookingRequestDto));
        assertThat(e.getMessage(), equalTo("Invalid user ID"));
    }

    @Test
    void failCreatingBookingWhenItemNotFound() {
        bookingRequestDto.setItemId(50L);
        ModelNotFoundException e = assertThrows(ModelNotFoundException.class,
                () -> bookingService.createBooking(2L, bookingRequestDto));
        assertThat(e.getMessage(), equalTo("Invalid item ID"));
    }

    @Test
    void failUpdatingBookingWhenBookingNotFound() {
        ModelNotFoundException e = assertThrows(ModelNotFoundException.class,
                () -> bookingService.updateBooking(1L, 20L, true));
        assertThat(e.getMessage(), equalTo("Booking not found."));
    }

    @Test
    void getBookingById() {
        bookingService.createBooking(2L, bookingRequestDto);
        BookingDto booking1 = bookingService.findBookingByUserId(1L, 2L);

        assertThat(booking1.getStatus(), equalTo(WAITING));
    }

    @Test
    void failGettingBookingByIdWhenBookingNotFound() {
        ModelNotFoundException e = assertThrows(ModelNotFoundException.class,
                () -> bookingService.findBookingByUserId(2L, 100L));
        assertThat(e.getMessage(), equalTo("Booking not found."));
    }

    @Test
    void faiGettingBookingByIdWithWrongRequest() {
        bookingService.createBooking(2L, bookingRequestDto);

        ModelNotFoundException e = assertThrows(ModelNotFoundException.class,
                () -> bookingService.findBookingByUserId(3L, 1L));
        assertThat(e.getMessage(), equalTo("Booking not found."));
    }

    @Test
    void findAllByBookerByStateAndStatus() {
        bookingService.createBooking(2L, bookingRequestDto);

        List<BookingDto> bookings = bookingService.findByBooker(2L, BookingState.valueOf("WAITING"), 0, 2);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }

    @Test
    void findAllByBookerByStatePast() {
        bookingService.createBooking(2L, bookingRequestDto);

        List<BookingDto> bookings = bookingService.findByBooker(2L, BookingState.valueOf("PAST"), 0, 2);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }

    @Test
    void findAllByBookerByStateRejected() {
        bookingService.createBooking(2L, bookingRequestDto);

        List<BookingDto> bookings = bookingService.findByBooker(2L, BookingState.valueOf("REJECTED"), 0, 2);

        assertThat(bookings.size(), equalTo(0));
    }

    @Test
    void findAllByBookerByStateFuture() {
        bookingService.createBooking(2L, bookingRequestDto);

        List<BookingDto> bookings = bookingService.findByBooker(2L, BookingState.valueOf("FUTURE"), 0, 2);

        assertThat(bookings.size(), equalTo(0));
    }

    @Test
    void findAllByBookerByStateCurrent() {
        bookingService.createBooking(2L, bookingRequestDto);

        List<BookingDto> bookings = bookingService.findByBooker(2L, BookingState.valueOf("CURRENT"), 0, 2);

        assertThat(bookings.size(), equalTo(0));
    }

    @Test
    void findAllByBookerByStateAll() {
        bookingService.createBooking(2L, bookingRequestDto);

        List<BookingDto> bookings = bookingService.findByBooker(2L, BookingState.valueOf("ALL"), 0, 2);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }

    @Test
    void findAllByBookerFailByWrongState() {
        bookingService.createBooking(2L, bookingRequestDto);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> bookingService.findByBooker(2L, BookingState.valueOf("NEVER"), 0, 2));

    }

    @Test
    void failGettingAllByBookerWithWrongBooker() {
        ModelNotFoundException e = assertThrows(ModelNotFoundException.class,
                () -> bookingService.findByBooker(10L, BookingState.valueOf("ALL"), 0, 2));
        assertThat(e.getMessage(), equalTo("Invalid user ID"));
    }

    @Test
    void findAllByOwnerByStateAndStatus() {
        bookingService.createBooking(2L, bookingRequestDto);

        List<BookingDto> bookings = bookingService.findByOwner(1L, BookingState.valueOf("WAITING"), 0, 2);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }

    @Test
    void findAllByOwnerByStatePast() {
        bookingService.createBooking(2L, bookingRequestDto);

        List<BookingDto> bookings = bookingService.findByOwner(1L, BookingState.valueOf("PAST"), 0, 2);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }

    @Test
    void findAllByOwnerByStateRejected() {
        bookingService.createBooking(2L, bookingRequestDto);

        List<BookingDto> bookings = bookingService.findByOwner(1L, BookingState.valueOf("REJECTED"), 0, 2);

        assertThat(bookings.size(), equalTo(0));
    }

    @Test
    void findAllByOwnerByStateFuture() {
        bookingService.createBooking(2L, bookingRequestDto);

        List<BookingDto> bookings = bookingService.findByOwner(1L, BookingState.valueOf("FUTURE"), 0, 2);

        assertThat(bookings.size(), equalTo(0));
    }

    @Test
    void findAllByOwnerByStateCurrent() {
        bookingService.createBooking(2L, bookingRequestDto);

        List<BookingDto> bookings = bookingService.findByOwner(1L, BookingState.valueOf("CURRENT"), 0, 2);

        assertThat(bookings.size(), equalTo(0));
    }

    @Test
    void findAllByOwnerByStateAll() {
        bookingService.createBooking(2L, bookingRequestDto);

        List<BookingDto> bookings = bookingService.findByOwner(1L, BookingState.valueOf("ALL"), 0, 2);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }

    @Test
    void findAllByOwnerFailByWrongState() {
        bookingService.createBooking(2L, bookingRequestDto);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> bookingService.findByOwner(2L, BookingState.valueOf("NEVER"), 0, 2));

    }

    @Test
    void failGettingAllByOwnerWithWrongOwner() {
        ModelNotFoundException e = assertThrows(ModelNotFoundException.class,
                () -> bookingService.findByOwner(10L, BookingState.valueOf("ALL"), 0, 2));
        assertThat(e.getMessage(), equalTo("Invalid user ID"));
    }
}
