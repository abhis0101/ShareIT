package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Reservation for user with id " + userId + " done");
        return bookingService.create(userId, bookingRequestDto);
    }

    @PatchMapping(path = "/{id}")
    public BookingDto update(@PathVariable(value = "id") Long id,
                             @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                             @RequestParam(value = "approved") boolean approved) {
        log.info("Reservation updated for user with id " + userId);
        return bookingService.update(id, userId, approved);
    }

    @GetMapping(path = "/{id}")
    public BookingDto findById(@PathVariable(value = "id") Long id,
                               @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Reservation with id " + id + " found");
        return bookingService.findById(id, userId);
    }

    @GetMapping
    public List<BookingDto> findByBooker(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                         @RequestParam(
                                                 value = "state",
                                                 required = false,
                                                 defaultValue = "ALL") BookingState state) {
        log.info("Received a list of all bookings for booker with id " + userId);
        return bookingService.findByBooker(userId, state);
    }

    @GetMapping(path = "/owner")
    public List<BookingDto> findByOwner(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                        @RequestParam(
                                                value = "state",
                                                required = false,
                                                defaultValue = "ALL") BookingState state) {
        log.info("Received a list of all reservations for all items owned by user with id " + userId);
        return bookingService.findByOwner(userId, state);
    }
}
