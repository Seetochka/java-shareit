package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(HEADER_USER_ID) long userId,
                                                    @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Creating itemRequest {}, userId={}", itemRequestDto, userId);
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUserId(@RequestHeader(HEADER_USER_ID) long userId) {
        log.info("Get itemRequests by userId={}", userId);
        return itemRequestClient.getItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequest(@RequestHeader(HEADER_USER_ID) long userId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get itemRequests where userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getAllItemRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(HEADER_USER_ID) long userId,
                                                     @PathVariable long requestId) {
        log.info("Get itemRequest {}, userId={}", requestId, userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
