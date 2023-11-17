package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.InvalidBookingException;
import ru.practicum.shareit.error.ModelNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.mapper.BookingMapper.*;
import static ru.practicum.shareit.booking.model.BookingStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "end");

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingRequestDto bookingRequestDto) {
        User booker = getUserById(userId);
        Item item = getItemById(bookingRequestDto.getItemId());
        if (item.getOwner().getId().equals(userId)) {
            throw new ModelNotFoundException("Item cannot be booked");
        }
        if (!item.getAvailable()) {
            throw new InvalidBookingException("Item with id " + item.getId() + " is not available for booking");
        }

        Booking booking = toBooking(bookingRequestDto, booker, item);
        booking.setStatus(WAITING);
        log.info("Booking for user with id " + userId + " done");
        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long id, Long userId, Boolean approved) {
        Booking booking = getBookingById(id);

        if (!booking.getItem().getOwner().getId().equals(userId) || booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ModelNotFoundException("Item cannot be booked");
        }
        if (!booking.getStatus().equals(WAITING)) {
            throw new InvalidBookingException("Unable to change booking status.");
        }
        if (approved) {
            booking.setStatus(APPROVED);
        } else {
            booking.setStatus(REJECTED);
        }
        log.info("Booking updated for user with id " + userId);
        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto findBookingByUserId(Long id, Long userId) {
        Booking booking = getBookingById(id);
        if (booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            log.info("Booking with id " + id + " found");
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new ModelNotFoundException("Unable to get information about the item");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findByBooker(Long userId, BookingState state, int from, int size) {
        getUserById(userId);

        List<Booking> bookings;
        Pageable page = PageRequest.of(from / size, size, sort);
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(), page);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, REJECTED, page);
                break;
            default:
                throw new InvalidBookingException("Unknown state: " + state);
        }
        log.info("Received a list of bookings for all items with booker id " + userId);
        return getListOfBookingDto(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findByOwner(Long userId, BookingState state, int from, int size) {
        getUserById(userId);

        List<Booking> bookings;
        Pageable page = PageRequest.of(from / size, size, sort);
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, page);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(), page);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, REJECTED, page);
                break;
            default:
                throw new InvalidBookingException("Unknown state: " + state);
        }
        log.info("Received a list of bookings for all items with owner id " + userId);
        return getListOfBookingDto(bookings);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ModelNotFoundException("Invalid user ID"));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new ModelNotFoundException("Invalid item ID"));
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new ModelNotFoundException("Booking not found."));
    }

}

