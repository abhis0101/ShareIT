package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable page);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime now, Pageable page);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now, Pageable page);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long userId, LocalDateTime now,
                                                                                 LocalDateTime now1, Pageable page);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus bookingStatus, Pageable page);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long bookerId, Pageable page);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime now,
                                                                                    LocalDateTime now1, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus bookingStatus, Pageable page);

    Optional<Booking> findTopByItemIdAndStatusAndStartIsAfterOrderByStart(Long itemId, BookingStatus bookingStatus, LocalDateTime now);

    Optional<Booking> findTopByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(Long itemId, BookingStatus bookingStatus, LocalDateTime now);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long userId, Long itemId, BookingStatus approved,
                                                                          LocalDateTime now);

}

