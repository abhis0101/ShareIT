package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.ModelNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.mapper.ItemRequestMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User user = getUserById(userId);
        ItemRequest itemRequest = toItemRequest(itemRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        log.info("Request for item added.");
        return toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getItemRequestById(Long requestId, Long userId) {
        getUserById(userId);
        ItemRequest itemRequest = getItemRequestById(requestId);
        ItemRequestDto itemRequestDto = toItemRequestDto(itemRequest);
        setItemsToItemRequestDto(itemRequestDto);
        log.info("Item Request data received for user with id " + userId);
        return itemRequestDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllItemRequestByUserId(Long userId) {
        getUserById(userId);
        List<ItemRequest> req = itemRequestRepository.findAllByRequesterIdOrderByCreatedAsc(userId);
        List<ItemRequestDto> requests = getItemRequestDtoList(req);
        setItems(requests, req);
        log.info("Received list of all user requests.");
        return requests;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllItemRequests(Long userId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        List<ItemRequest> req = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedAsc(userId, page);
        List<ItemRequestDto> requests = getItemRequestDtoList(req);
        setItems(requests, req);
        log.info("Retrieved a list of all requests created by users.");
        return requests;
    }

    private void setItems(List<ItemRequestDto> requests, List<ItemRequest> req) {

        List<Item> items = itemRepository.findAllByRequestIn(req);
        for (ItemRequestDto itemReg : requests) {
            List<Item> items1 = items.stream()
                    .filter(i -> Objects.equals(i.getRequest().getId(), itemReg.getId()))
                    .collect(Collectors.toList());
            itemReg.setItems(ItemMapper.getListItemDto(items1));
        }
    }

    private void setItemsToItemRequestDto(ItemRequestDto itemRequestDto) {
        itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequestDto.getId())
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ModelNotFoundException("Invalid user ID."));
    }

    private ItemRequest getItemRequestById(Long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() ->
                new ModelNotFoundException("Invalid request ID."));
    }
}
