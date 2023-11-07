package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Validated
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Booking for user with id " + userId + " done.");
        return bookingService.createBooking(userId, bookingRequestDto);
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingDto updateBooking(@PathVariable Long bookingId,
                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                    @RequestParam(value = "approved") Boolean approved) {
        log.info("Booking update for user with id " + userId);
        return bookingService.updateBooking(bookingId, userId, approved);
    }

    @GetMapping(path = "/{bookingId}")
    public BookingDto findBookingByUserId(@PathVariable Long bookingId,
                                          @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Booking with id " + bookingId + " found");
        return bookingService.findBookingByUserId(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findByBooker(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "ALL") BookingState state,
                                         @RequestParam(defaultValue = "0") @Min(0) int from,
                                         @RequestParam(defaultValue = "20") @Min(1) int size) {
        log.info("Received a list of bookings for all item's with owner id " + userId);
        return bookingService.findByBooker(userId, state, from, size);
    }

    @GetMapping(path = "/owner")
    public List<BookingDto> findByOwner(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                        @RequestParam(defaultValue = "ALL") BookingState state,
                                        @RequestParam(defaultValue = "0") @Min(0) int from,
                                        @RequestParam(defaultValue = "20") @Min(1) int size) {
        log.info("Received a list of bookings for all item's with booker id " + userId);
        return bookingService.findByOwner(userId, state, from, size);
    }
}
