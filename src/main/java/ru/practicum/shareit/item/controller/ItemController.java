package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Item created with owner id: " + userId);
        return itemService.createItem(userId, itemDto);
    }


    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("These items have been updated.");
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoResponse getItemByUserId(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received item with id: " + itemId);
        return itemService.getItemByUserId(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoResponse> getItemListByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received a list of all items for user with ID: " + userId);
        return itemService.getItemListByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(value = "text") String text) {
        log.info("Found item with keyword: " + text);
        return itemService.search(text);
    }

    @PostMapping(path = "/{itemId}/comment")
    public CommentDto saveComment(@PathVariable Long itemId,
                                  @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                  @RequestBody @Valid CommentRequestDto commentRequestDto) {
        return itemService.saveComment(itemId, userId, commentRequestDto);
    }

}
