package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

@SpringBootTest
public class ItemMapperTest {

    private Item item;
    private ItemDto itemDto;
    private ItemDtoShort itemDtoShort;

    @BeforeEach
    void before() {

        ItemRequest itemRequest = ItemRequest
                .builder()
                .id(1L)
                .description("description")
                .build();

        item = Item
                .builder()
                .id(1L)
                .name("Drill")
                .description("drilling the wall")
                .available(true)
                .request(itemRequest)
                .build();

        itemDto = ItemMapper.toItemDto(item);
        itemDtoShort = ItemMapper.toItemDtoShort(item);

    }

    @Test
    void toItemDto() {
        Assertions.assertNotNull(item);
        Assertions.assertEquals(item.getId(), itemDto.getId());
        Assertions.assertEquals(item.getName(), itemDto.getName());
        Assertions.assertEquals(item.getDescription(), itemDto.getDescription());
        Assertions.assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void toItemDtoShort() {
        Assertions.assertNotNull(item);
        Assertions.assertEquals(item.getId(), itemDtoShort.getId());
        Assertions.assertEquals(item.getName(), itemDtoShort.getName());
        Assertions.assertEquals(item.getDescription(), itemDtoShort.getDescription());
        Assertions.assertEquals(item.getAvailable(), itemDtoShort.getAvailable());
    }
}
