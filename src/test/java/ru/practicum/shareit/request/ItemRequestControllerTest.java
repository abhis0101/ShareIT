package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.ModelNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureWebMvc
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;
    private ItemRequestDto itemRequestDto;
    private final LocalDateTime timestamp1 = LocalDateTime.of(2022, 10, 5, 10, 30);

    @BeforeEach
    void init() {

        ItemDto itemShortDto = ItemDto
                .builder()
                .id(1L)
                .name("Drill")
                .description("drilling the wall")
                .available(true)
                .requestId(1L)
                .build();

        itemRequestDto = ItemRequestDto
                .builder()
                .id(1L)
                .description("request description")
                .created(timestamp1)
                .items(List.of(itemShortDto))
                .build();
    }

    @Test
    void createItemRequestExpectedStatus200() throws Exception {
        when(itemRequestService.createItemRequest(any(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void createItemRequestWithWrongUserExpectedStatus404() throws Exception {
        when(itemRequestService.createItemRequest(any(), anyLong()))
                .thenThrow(new ModelNotFoundException("Not found exception"));

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Not found exception")));
    }

    @Test
    void createItemRequestWithoutDescriptionExpectedStatus400() throws Exception {
        ItemRequestDto badRequest = new ItemRequestDto(1L, null, timestamp1, null);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(badRequest))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllItemRequestByUserIdExpectedStatus200() throws Exception {
        when(itemRequestService.getAllItemRequestByUserId(1L)).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getAllByUserIdWithWrongIdExpectedStatus404() throws Exception {
        when(itemRequestService.getAllItemRequestByUserId(anyLong()))
                .thenThrow(new ModelNotFoundException("Not found exception"));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Not found exception")));
    }

    @Test
    void getAllItemRequestsExpectedStatus200() throws Exception {
        when(itemRequestService.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all?from=2&size=2")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getAllItemRequestsWithWrongFromExpectedStatus500() throws Exception {
        mvc.perform(get("/requests/all?from=-2&size=2")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllRequestsWithWrongSizeExpectedStatus500() throws Exception {
        mvc.perform(get("/requests/all?from=2&size=0")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getItemRequestByIdExpectedStatus200() throws Exception {
        when(itemRequestService.getItemRequestById(1L, 1L)).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{id}", 1)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getItemRequestByIdExpectedStatus404() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenThrow(new ModelNotFoundException("Not found exception"));

        mvc.perform(get("/requests/{id}", 20)
                        .header("X-Sharer-User-Id", 20L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Not found exception")));
    }
}
