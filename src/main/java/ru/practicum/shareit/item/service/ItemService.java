package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


public interface ItemService {

    List<ItemDto> findAllItems();

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    ItemDto getItemByUserId(Long itemId, Long userId);

    List<ItemDto> getItemListByUserId(Long userId, int from, int size);

    List<ItemDto> search(String text, int from, int size);

    CommentDto saveComment(Long itemId, Long userId, CommentDto commentDto);

}
