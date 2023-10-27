package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDtoShort item;
    private UserDtoShort booker;
    private BookingStatus status;
}
