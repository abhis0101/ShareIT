package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;


    private User user;
    private User user1;
    private Item item;
    private Comment comment;
    private ItemRequest itemRequest;

    @BeforeEach
    void init() {

        user = User.builder()
                .name("Oleg")
                .email("oleg@email.com")
                .build();

        user1 = User.builder()
                .name("Ivan")
                .email("ivan@email.com")
                .build();

        item = Item.builder()
                .name("Hammer")
                .description("hammering the nail")
                .available(true)
                .owner(user)
                .build();

        itemRequest = ItemRequest
                .builder()
                .description("request description")
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        comment = Comment.builder()
                .text("comment")
                .item(item)
                .author(user1)
                .created(LocalDateTime.now())
                .build();

    }


    @Test
    void findCommentsByItemId() {
        userRepository.save(user1);
        userRepository.save(user);
        itemRequest.setRequester(user);
        itemRequestRepository.save(itemRequest);
        item.setRequest(itemRequest);
        itemRepository.save(item);

        Comment comment1 = Comment.builder()
                .text("comment 1")
                .item(item)
                .author(user1)
                .created(LocalDateTime.now())
                .build();
        Comment comment2 = Comment.builder()
                .text("comment 2")
                .item(item)
                .author(user1)
                .created(LocalDateTime.now())
                .build();

        commentRepository.saveAll(Arrays.asList(comment1, comment2));

        Set<Long> itemIds = new HashSet<>();
        itemIds.add(item.getId());

        Set<Comment> comments = commentRepository.findCommentsByItemId(itemIds);

        assertThat(comments.size(), equalTo(2));
        assertThat(comments.stream().map(Comment::getItem).collect(Collectors.toSet()), hasItem(item));
    }

    @Test
    void findAllByItemId() {
        userRepository.save(user);
        userRepository.save(user1);
        itemRepository.save(item);
        commentRepository.save(comment);

        assertThat(commentRepository.findAllByItemId(item.getId()).size(), equalTo(1));
    }
}
