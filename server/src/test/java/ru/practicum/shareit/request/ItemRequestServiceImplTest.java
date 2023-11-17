package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.error.ModelNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceImplTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void init() {

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Oleg")
                .email("oleg@email.com")
                .build();
        userService.createUser(userDto);

        itemRequestDto = ItemRequestDto
                .builder()
                .id(1L)
                .description("request description")
                .build();
    }

    @Test
    void createItemRequest() {
        itemRequestService.createItemRequest(itemRequestDto, 1L);

        TypedQuery<ItemRequest> query = em
                .createQuery("SELECT i FROM ItemRequest i WHERE i.description = :description", ItemRequest.class);
        ItemRequest checkedRequest = query
                .setParameter("description", itemRequestDto.getDescription())
                .getSingleResult();

        assertThat(checkedRequest.getId(), equalTo(1L));
        assertThat(checkedRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
    }

    @Test
    void failCreatingItemRequestWithWrongUserId() {
        ModelNotFoundException e = assertThrows(ModelNotFoundException.class,
                () -> itemRequestService.createItemRequest(itemRequestDto, 20L));
        assertThat(e.getMessage(), equalTo("Invalid user ID."));
    }

    @Test
    void getItemRequestById() {
        itemRequestService.createItemRequest(itemRequestDto, 1L);

        ItemRequestDto request = itemRequestService.getItemRequestById(1L, 1L);

        assertThat(request.getDescription(), equalTo("request description"));
        assertThat(request.getId(), equalTo(1L));
    }

    @Test
    void failGettingItemRequestWithWrongUserId() {
        itemRequestService.createItemRequest(itemRequestDto, 1L);

        ModelNotFoundException e = assertThrows(ModelNotFoundException.class,
                () -> itemRequestService.getItemRequestById(1L, 20L));
        assertThat(e.getMessage(), equalTo("Invalid user ID."));
    }

    @Test
    void failGettingItemRequestWithWrongId() {
        itemRequestService.createItemRequest(itemRequestDto, 1L);

        ModelNotFoundException e = assertThrows(ModelNotFoundException.class,
                () -> itemRequestService.getItemRequestById(20L, 1L));
        assertThat(e.getMessage(), equalTo("Invalid request ID."));
    }

    @Test
    void getAllItemRequestByUserId() {
        itemRequestService.createItemRequest(itemRequestDto, 1L);
        List<ItemRequestDto> requests = itemRequestService.getAllItemRequestByUserId(1L);
        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0).getDescription(), equalTo("request description"));
    }

    @Test
    void failGettingAllRequestWithWrongUserId() {
        ModelNotFoundException e = assertThrows(ModelNotFoundException.class,
                () -> itemRequestService.getAllItemRequestByUserId(20L));

        assertThat(e.getMessage(), equalTo("Invalid user ID."));
    }

    @Test
    void getAllItemRequests() {

        UserDto userDto = UserDto.builder()
                .id(2L)
                .name("Ivan")
                .email("ivan@email.com")
                .build();
        userService.createUser(userDto);

        ItemRequestDto itemRequestDto = ItemRequestDto
                .builder()
                .id(2L)
                .description("request")
                .build();
        itemRequestService.createItemRequest(itemRequestDto, 2L);

        List<ItemRequestDto> requests = itemRequestService.getAllItemRequests(1L, 0, 2);

        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0).getDescription(), equalTo("request"));
    }
}
