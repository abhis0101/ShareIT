package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;


public interface ItemService {

    List<ItemDto> findAllItems();

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    ItemDtoResponse getItemByUserId(Long itemId, Long userId);

    List<ItemDtoResponse> getItemListByUserId(Long userId);

    List<ItemDto> search(String query);

    CommentDto saveComment(Long itemId, Long userId, CommentRequestDto commentRequestDto);

}
