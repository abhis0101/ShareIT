package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.mapper.ItemMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<ItemDto> findAllItems() {
        log.info("List of items received.");
        return getListItemDto(itemRepository.findAllItems());
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, int userId) {
        User owner = userRepository.getUserById(userId);
        Item item = toItem(itemDto, owner);
        checkItems(item);
        log.info("Item added.");
        return toItemDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto updateItem(int itemId, ItemDto itemDto, Integer userId) {
        Item item = itemRepository.getItemById(itemId);
        if (!userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("Invalid item ID.");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        log.info("These items have been updated.");
        return toItemDto(itemRepository.updateItem(item));
    }

    @Override
    public ItemDto getItemById(int itemId) {
        log.info("Received item with ID: " + itemId);
        return toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemByUserId(int userId) {
        log.info("Received a list of all things of the user with ID: " + userId);
        return itemRepository.findAllItems().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemByText(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        log.info("Item found with keyword: " + text);
        String lowerCaseText = text.toLowerCase();
        return itemRepository.findAllItems().stream()
                .filter(i -> i.getAvailable() &&
                        (i.getName().toLowerCase().contains(lowerCaseText) ||
                                i.getDescription().toLowerCase().contains(lowerCaseText))).map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkItems(Item item) {
        if (item.getAvailable() == null || item.getName().isBlank() || item.getDescription() == null ||
                item.getDescription().isBlank() || item.getName() == null) {
            throw new ValidationException("Wrong data.");
        }
    }
}
