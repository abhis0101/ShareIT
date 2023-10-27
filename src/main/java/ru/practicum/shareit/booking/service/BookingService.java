package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto create(Long userId, BookingRequestDto bookingRequestDto);

    BookingDto update(Long id, Long userId, boolean approved);

    BookingDto findById(Long id, Long userId);

    List<BookingDto> findByBooker(Long userId, BookingState state);

    List<BookingDto> findByOwner(Long userId, BookingState state);
}
