package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestJsonTest {
    @Autowired
    JacksonTester<ItemRequestDto> json;

    @Test
    void serializeItemRequestDtoToJson() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestDto
                .builder()
                .id(1L)
                .description("need a ladder")
                .created(LocalDateTime.of(2021, 6, 16, 11, 30, 15))
                .build();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("need a ladder");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(LocalDateTime.of(2021, 6, 16, 11, 30, 15).toString());
    }
}
