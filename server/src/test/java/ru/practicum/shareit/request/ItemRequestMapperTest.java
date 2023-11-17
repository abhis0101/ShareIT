package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.request.mapper.ItemRequestMapper.getItemRequestDtoList;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.toItemRequestDto;

@SpringBootTest
public class ItemRequestMapperTest {

    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private List<ItemRequestDto> itemRequestsDto;

    @BeforeEach
    void before() {
        itemRequest = ItemRequest
                .builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .requester(new User(1L, "Ivan", "Ivan@mail.ru"))
                .build();

        itemRequestDto = toItemRequestDto(itemRequest);
        itemRequestsDto = getItemRequestDtoList(List.of(itemRequest));
    }

    @Test
    void fromItemRequestDtoToItemRequest() {
        Assertions.assertNotNull(itemRequestDto);
        Assertions.assertEquals(itemRequestDto.getId(), itemRequest.getId());
        Assertions.assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
        Assertions.assertEquals(itemRequestDto.getCreated(), itemRequest.getCreated());
    }

    @Test
    void fromItemToItemRequestsDto() {
        Assertions.assertNotNull(itemRequestsDto.get(0));

        Assertions.assertEquals(itemRequestsDto.get(0).getId(), itemRequest.getId());
        Assertions.assertEquals(itemRequestsDto.get(0).getDescription(), itemRequest.getDescription());
        Assertions.assertEquals(itemRequestsDto.get(0).getCreated(), itemRequest.getCreated());
    }
}
