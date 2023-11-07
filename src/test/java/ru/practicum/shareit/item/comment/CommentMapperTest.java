package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@SpringBootTest
public class CommentMapperTest {
    private Comment comment;
    private CommentDto commentDto;
    private List<CommentDto> commentsDto;

    @BeforeEach
    void before() {

        User user = User
                .builder()
                .id(1L)
                .name("Ivan")
                .email("Ivan@mail.ru")
                .build();

        Item item = Item
                .builder()
                .id(1L)
                .name("Drill")
                .description("drilling the wall")
                .available(true)
                .build();

        commentDto = CommentDto
                .builder()
                .id(1L)
                .text("text")
                .authorName("Ivan")
                .build();

        comment = CommentMapper.toComment(commentDto, user, item);
        commentsDto = CommentMapper.getCommentDtoList(List.of(comment));
    }

    @Test
    void toComment() {
        Assertions.assertNotNull(comment);
        Assertions.assertEquals(comment.getId(), commentDto.getId());
        Assertions.assertEquals(comment.getText(), commentDto.getText());
        Assertions.assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
    }

    @Test
    void toCommentDto() {
        Assertions.assertNotNull(comment);
        Assertions.assertEquals(CommentMapper.toCommentDto(comment).getId(), commentDto.getId());
        Assertions.assertEquals(CommentMapper.toCommentDto(comment).getText(), commentDto.getText());
        Assertions.assertEquals(CommentMapper.toCommentDto(comment).getAuthorName(), commentDto.getAuthorName());
    }

    @Test
    void toCommentsDtoList() {
        Assertions.assertNotNull(commentsDto.get(0));

        Assertions.assertEquals(commentsDto.get(0).getId(), commentDto.getId());
        Assertions.assertEquals(commentsDto.get(0).getText(), commentDto.getText());
        Assertions.assertEquals(commentsDto.get(0).getAuthorName(), commentDto.getAuthorName());
    }
}
