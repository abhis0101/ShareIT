package ru.practicum.shareit.request;

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
@RequiredArgsConstructor
@RequestMapping("/requests")
@Validated
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Item request created by user with id " + userId + " from local client");
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    ResponseEntity<Object> getAllItemRequestByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Got list of item requests made by user with id " + userId + " from local client");
        return itemRequestClient.getAllItemRequestByUserId(userId);
    }

    @GetMapping("/all")
    ResponseEntity<Object> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Got List of item requests for user with id " + userId + "from " + from + "to " + size);
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.info("Got Item request by id " + requestId + " from local client");
        return itemRequestClient.getItemRequestById(requestId, userId);
    }
}