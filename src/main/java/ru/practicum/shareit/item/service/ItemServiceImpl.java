package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.InvalidBookingException;
import ru.practicum.shareit.error.ModelNotFoundException;
import ru.practicum.shareit.error.UserHaveNotAccessException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.mapper.ItemMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    public List<ItemDto> findAllItems() {
        log.info("List of items received.");
        return getListItemDto(itemRepository.findAll());
    }

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = getUserById(userId);
        Item createItem = toItem(itemDto);
        createItem.setOwner(user);
        log.info("Item added");
        return toItemDto(itemRepository.save(createItem));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item = getById(itemId);
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new UserHaveNotAccessException("Invalid user ID.");
        }
        Item updatedItem = itemRepository.save(checksItems(item, itemDto));
        log.info("Item with id " + updatedItem.getId() + " updated");
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoResponse getItemByUserId(Long itemId, Long userId) {
        Item item = getById(itemId);
        BookingShortDto nextBooking = null;
        BookingShortDto lastBooking = null;
        if (Objects.equals(item.getOwner().getId(), userId)) {
            nextBooking = bookingRepository.findTopByItemIdAndStatusAndStartIsAfterOrderByStart(itemId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            lastBooking = bookingRepository.findTopByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(itemId,
                    BookingStatus.APPROVED, LocalDateTime.now());
        }
        List<Comment> comments = commentRepository.findByItemId(itemId);

        log.info("Item with name " + item.getName() + " requested");

        return ItemMapper.toItemDtoResponse(item, nextBooking, lastBooking, CommentMapper.listToDtoList(comments));
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoResponse> getItemListByUserId(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(userId);
        if (items.isEmpty()) {
            log.info("List of items for user with id " + userId + " empty");
            return Collections.emptyList();
        }
        log.info("Received a list of items for user with ID: " + userId);
        return items.stream().map(i -> getItemByUserId(i.getId(), userId)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String query) {
        if (query.isBlank()) {
            log.info("Empty query parameter");
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.search(query);
        if (items.isEmpty()) {
            log.info("No items were found with the given keyword " + query);
            return Collections.emptyList();
        }
        log.info("Received a list of items with the given order of keywords " + query);
        return ItemMapper.getListItemDto(items);
    }

    @Override
    @Transactional
    public CommentDto saveComment(Long itemId, Long userId, CommentRequestDto commentRequestDto) {
        Item item = getById(itemId);
        User user = getUserById(userId);
        Booking booking = bookingRepository.findTopByItemIdAndBookerIdAndStatusAndEndIsBefore(
                        itemId, userId, BookingStatus.APPROVED, LocalDateTime.now())
                .orElseThrow(() -> new InvalidBookingException("User with id " + userId + " has not previously booked an item with id " + itemId));

        Comment comment = CommentMapper.toComment(item, user, commentRequestDto.getText());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ModelNotFoundException("Invalid user ID."));
    }

    private Item getById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new ModelNotFoundException("Invalid ID."));
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data sent");
        }
    }


}
