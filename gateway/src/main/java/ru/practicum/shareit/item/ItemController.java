package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("Item created with owner id: " + userId + " from local client");
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    ResponseEntity<Object> updateItem(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestBody ItemDto itemDto) {
        log.info("These items have been updated from the local client");
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    ResponseEntity<Object> getItemByUserId(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received item with id: " + itemId + " from local client");
        return itemClient.getItemByUserId(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemListByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("A list of all items for user with ID: " + userId + " received from local client");
        return itemClient.getItemListByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Found item by given keyword: " + text + " from local client");
        return itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                              @RequestBody @Valid CommentDto commentDto) {
        log.info("Saved comment from user with id " + userId + " from local client");
        return itemClient.saveComment(userId, itemId, commentDto);
    }
}
