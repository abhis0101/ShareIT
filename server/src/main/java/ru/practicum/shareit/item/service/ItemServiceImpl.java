package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.InvalidBookingException;
import ru.practicum.shareit.error.ModelNotFoundException;
import ru.practicum.shareit.error.UserHaveNotAccessException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.item.comment.mapper.CommentMapper.*;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;


    @Override
    public List<ItemDto> findAllItems() {
        log.info("List of items received.");
        return getListItemDto(itemRepository.findAll());
    }

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User savedUser = getUserById(userId);
        Item item = toItem(itemDto);
        item.setOwner(savedUser);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = getItemRequestById(itemDto.getRequestId());
            item.setRequest(itemRequest);
        }
        log.info("Item added.");
        return toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item = getById(itemId);
        if (!userId.equals(item.getOwner().getId())) {
            throw new UserHaveNotAccessException("Invalid user ID.");
        }
        Item updatedItem = itemRepository.save(checksItems(item, itemDto));
        log.info("Item with id " + updatedItem.getId() + " updated");
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemByUserId(Long itemId, Long userId) {

        Item item = getById(itemId);
        ItemDto itemDto = toItemDto(item);
        populateItemDto(itemDto);
        if (!item.getOwner().getId().equals(userId)) {
            itemDto.setLastBooking(null);
            itemDto.setNextBooking(null);
        }
        log.info("Item with id " + item.getId() + " requested");
        return itemDto;
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemListByUserId(Long userId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);

        List<Item> items = itemRepository.findAllByOwnerIdOrderById(userId, page).stream()
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());

        List<ItemDto> itemsDto = this.setBookings(items);
        this.setComments(itemsDto);
        log.info("Received a list of all the user's items.");
        return itemsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text, int from, int size) {
        if (text.isBlank()) {
            log.info("Empty query parameter");
            return new ArrayList<>();
        }
        String query = text.toLowerCase();
        Pageable page = PageRequest.of(from / size, size);
        Page<Item> items = itemRepository.search(query, page);
        if (items.isEmpty()) {
            log.info("No items were found for the given letters " + query);
            return new ArrayList<>();
        }
        log.info("Received a list of items in the given order of letters " + query);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto saveComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = getUserById(userId);
        Item item = getById(itemId);

        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId, APPROVED,
                LocalDateTime.now()).isEmpty()) {
            throw new InvalidBookingException("Unable to add a comment.");
        }

        Comment comment = toComment(commentDto, user, item);
        log.info("Comment added.");
        return toCommentDto(commentRepository.save(comment));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ModelNotFoundException("Invalid user ID."));
    }

    private Item getById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new ModelNotFoundException("Invalid ID."));
    }

    private ItemRequest getItemRequestById(Long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() ->
                new ModelNotFoundException("Invalid request ID."));
    }

    private Item checksItems(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null && !itemDto.getName().equals(item.getName())) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().equals(item.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null && itemDto.getAvailable() != item.getAvailable()) {
            item.setAvailable(itemDto.getAvailable());
        }
        isValid(item);
        return item;
    }

    private void isValid(Item item) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        if (!violations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data transmitted");
        }
    }

    private void populateItemDto(ItemDto itemDto) {
        BookingShortDto lastBooking = getLastBooking(itemDto.getId());
        BookingShortDto nextBooking = getNextBooking(itemDto.getId());

        List<CommentDto> comments = getCommentDtoList(commentRepository.findAllByItemId(itemDto.getId()));

        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setComments(comments);
    }

    private BookingShortDto getNextBooking(Long itemId) {
        return bookingRepository.findTopByItemIdAndStatusAndStartIsAfterOrderByStart(itemId, APPROVED, LocalDateTime.now())
                .map(BookingMapper::toBookingShortDto)
                .orElse(null);
    }

    private BookingShortDto getLastBooking(Long itemId) {
        return bookingRepository.findTopByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(itemId, APPROVED, LocalDateTime.now())
                .map(BookingMapper::toBookingShortDto)
                .orElse(null);
    }

    private void setComments(List<ItemDto> items) {
        Map<Long, ItemDto> itemsDto = new HashMap<>();
        items.forEach(item -> itemsDto.put(item.getId(), item));

        Set<Comment> comments = new HashSet<>(commentRepository.findCommentsByItemId(itemsDto.keySet()));

        if (!itemsDto.isEmpty()) {
            comments.forEach(comment -> Optional.ofNullable(itemsDto.get(comment.getItem().getId()))
                    .ifPresent(i -> i.getComments().add(toCommentDto(comment))));
        }
    }

    private List<ItemDto> setBookings(List<Item> items) {
        List<ItemDto> itemsDto = getListItemDto(items);

        Set<Booking> bookings = new HashSet<>(bookingRepository.findAll());

        if (!itemsDto.isEmpty()) {
            itemsDto.forEach(item -> {

                Optional<Booking> nextBooking = bookings.stream()
                        .filter(booking -> booking.getItem().getId().equals(item.getId()))
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .min(Comparator.comparing(Booking::getStart));

                Optional<Booking> lastBooking = bookings.stream()
                        .filter(booking -> booking.getItem().getId().equals(item.getId()))
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .max(Comparator.comparing(Booking::getEnd));

                item.setNextBooking(nextBooking
                        .map(BookingMapper::toBookingShortDto)
                        .orElse(null));
                item.setLastBooking(lastBooking
                        .map(BookingMapper::toBookingShortDto)
                        .orElse(null));
            });
        }
        return itemsDto;
    }

}
