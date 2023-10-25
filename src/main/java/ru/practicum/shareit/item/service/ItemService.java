package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


public interface ItemService {

    List<ItemDto> findAllItems();

    ItemDto createItem(ItemDto itemDto, int userId);

    ItemDto updateItem(int itemId, ItemDto itemDto, Integer userId);

    ItemDto getItemById(int itemId);

    List<ItemDto> getItemByUserId(int userId);

    List<ItemDto> getItemByText(String text);


}
