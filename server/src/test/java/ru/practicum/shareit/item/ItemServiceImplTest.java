package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.TransactionSystemException;
import ru.practicum.shareit.error.ModelNotFoundException;
import ru.practicum.shareit.error.UserHaveNotAccessException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceImplTest {

    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;


    private final UserDto userDto = UserDto
            .builder()
            .id(1L)
            .name("Petr")
            .email("petr@yandex.ru")
            .build();

    private final ItemDto itemDto = ItemDto
            .builder()
            .id(1L)
            .name("Hammer")
            .description("hammering the nail")
            .available(true)
            .build();

    private final CommentDto commentDto = CommentDto
            .builder()
            .id(1L)
            .text("new comment")
            .build();

    @Test
    void createItem() {

        userService.createUser(userDto);
        itemService.createItem(1L, itemDto);

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.name = :name", Item.class);
        Item checkedItem = query
                .setParameter("name", itemDto.getName())
                .getSingleResult();

        assertThat(checkedItem.getId(), equalTo(1L));
        assertThat(checkedItem.getName(), equalTo(itemDto.getName()));
        assertThat(checkedItem.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void failCreatingItemWithWrongUserId() {

        userService.createUser(userDto);
        itemService.createItem(1L, itemDto);

        ModelNotFoundException e = assertThrows(ModelNotFoundException.class,
                () -> itemService.createItem(20L, itemDto));
        assertThat(e.getMessage(), equalTo("Invalid user ID."));
    }

    @Test
    void failCreatingItemWithWrongItemRequestId() {

        userService.createUser(userDto);
        itemService.createItem(1L, itemDto);
        itemDto.setRequestId(20L);

        ModelNotFoundException e = assertThrows(ModelNotFoundException.class,
                () -> itemService.createItem(1L, itemDto));
        assertThat(e.getMessage(), equalTo("Invalid request ID."));
    }

    @Test
    void failCreatingItemWithEmptyDescription() {

        userService.createUser(userDto);
        itemService.createItem(1L, itemDto);
        itemDto.setDescription(null);

        TransactionSystemException e = assertThrows(TransactionSystemException.class,
                () -> itemService.createItem(1L, itemDto));

    }

    @Test
    void failCreatingItemWithEmptyAvailable() {

        userService.createUser(userDto);
        itemService.createItem(1L, itemDto);
        itemDto.setAvailable(null);

        TransactionSystemException e = assertThrows(TransactionSystemException.class,
                () -> itemService.createItem(1L, itemDto));

    }

    @Test
    void updateItem() {

        userService.createUser(userDto);
        itemService.createItem(1L, itemDto);

        itemDto.setName("Sledgehammer");
        itemDto.setDescription("more reliable than a hammer");
        itemService.updateItem(1L, itemDto, 1L);

        assertThat("Sledgehammer", equalTo(itemDto.getName()));
        assertThat("more reliable than a hammer", equalTo(itemDto.getDescription()));
    }

    @Test
    void failUpdatingItemWithWrongUserId() {
        userService.createUser(userDto);
        itemService.createItem(1L, itemDto);

        itemDto.setId(1L);
        UserHaveNotAccessException e = assertThrows(UserHaveNotAccessException.class,
                () -> itemService.updateItem(1L, itemDto, 20L));
        assertThat(e.getMessage(), equalTo("Invalid user ID."));
    }

    @Test
    void failUpdatingItemWithWrongItemId() {
        userService.createUser(userDto);
        itemService.createItem(1L, itemDto);

        itemDto.setId(50L);
        ModelNotFoundException e = assertThrows(ModelNotFoundException.class,
                () -> itemService.updateItem(50L, itemDto, 1L));
        assertThat(e.getMessage(), equalTo("Invalid ID."));
    }

    @Test
    void getItemByIdWithNoBookings() {
        userService.createUser(userDto);
        itemService.createItem(1L, itemDto);
        ItemDto item = itemService.getItemByUserId(1L, 1L);

        assertThat(item.getName(), equalTo("Hammer"));
        assertThat(item.getLastBooking(), nullValue());
        assertThat(item.getNextBooking(), nullValue());
    }

    @Test
    void getItemByUserId() {
        userService.createUser(userDto);
        itemService.createItem(1L, itemDto);

        List<ItemDto> items = itemService.getItemListByUserId(1L, 0, 2);
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getName(), equalTo("Hammer"));
    }

    @Test
    void failToGetItemByIdWrongItem() {
        ModelNotFoundException e = assertThrows(ModelNotFoundException.class,
                () -> itemService.getItemByUserId(20L, 1L));
        assertThat(e.getMessage(), equalTo("Invalid ID."));
    }

    @Test
    void searchItemByText() {
        userService.createUser(userDto);
        itemService.createItem(1L, itemDto);

        List<ItemDto> searched = itemService.search("HamMer", 0, 3);

        assertThat(searched.size(), equalTo(1));
        assertThat(searched.get(0).getDescription(), equalTo("hammering the nail"));
    }

    @Test
    void failCreatingCommentWithWrongItemId() {
        userService.createUser(userDto);

        ModelNotFoundException e = assertThrows(ModelNotFoundException.class,
                () -> itemService.saveComment(20L, 1L, commentDto));

        assertThat(e.getMessage(), equalTo("Invalid ID."));
    }

    @Test
    void failCreatingCommentWithWrongUserId() {
        ModelNotFoundException e = assertThrows(ModelNotFoundException.class,
                () -> itemService.saveComment(1L, 20L, commentDto));

        assertThat(e.getMessage(), equalTo("Invalid user ID."));
    }
}
