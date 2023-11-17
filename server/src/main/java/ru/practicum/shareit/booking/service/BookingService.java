package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(Long userId, BookingRequestDto bookingRequestDto);

    BookingDto updateBooking(Long id, Long userId, Boolean approved);

    BookingDto findBookingByUserId(Long id, Long userId);

    List<BookingDto> findByBooker(Long userId, BookingState state, int from, int size);

    List<BookingDto> findByOwner(Long userId, BookingState state, int from, int size);
}
