package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .item(ItemMapper.toItemDtoShort(booking.getItem()))
                .booker(UserMapper.toUserShortDto(booking.getBooker()))
                .end(booking.getEnd())
                .start(booking.getStart())
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingRequestDto bookingRequestDto, User user, Item item) {
        return Booking.builder()
                .item(item)
                .booker(user)
                .end(bookingRequestDto.getEnd())
                .start(bookingRequestDto.getStart())
                .status(BookingStatus.WAITING)
                .build();
    }

    public static List<BookingDto> getListOfBookingDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
