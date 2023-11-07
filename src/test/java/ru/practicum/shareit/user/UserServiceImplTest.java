package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.error.ModelNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
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
public class UserServiceImplTest {
    private final EntityManager em;
    private final UserService service;

    @Test
    void createUser() {

        UserDto userDto = new UserDto(1L, "Petr", "petr@yandex.ru");
        service.createUser(userDto);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUser() {

        UserDto userDto = new UserDto(1L, "Ivan", "ivan@yandex.ru");
        service.createUser(userDto);

        userDto.setName("Vanya");
        userDto.setEmail("vanya@yandex.ru");
        service.updateUser(1L, userDto);

        assertThat("Vanya", equalTo(userDto.getName()));
        assertThat("vanya@yandex.ru", equalTo(userDto.getEmail()));
    }

    @Test
    void failUpdateUser() {
        UserDto userDto = new UserDto(1L, "Ivan", "ivan@yandex.ru");
        service.createUser(userDto);

        ModelNotFoundException e = assertThrows(ModelNotFoundException.class, () -> service.updateUser(66L, userDto));
        assertThat(e.getMessage(), equalTo("User with id - 66 not found!"));
    }

    @Test
    void getUserById() {

        UserDto userDto = new UserDto(1L, "Oleg", "oleg@yandex.ru");
        service.createUser(userDto);
        UserDto userDto1 = new UserDto(2L, "Roma", "roma@yandex.ru");
        service.createUser(userDto1);

        UserDto userDto2 = service.getUserById(1L);
        UserDto userDto3 = service.getUserById(2L);

        assertThat("Oleg", equalTo(userDto2.getName()));
        assertThat("Roma", equalTo(userDto3.getName()));
    }

    @Test
    void failGettingUserById() {

        ModelNotFoundException e = assertThrows(ModelNotFoundException.class, () -> service.getUserById(55L));
        assertThat(e.getMessage(), equalTo("User with id - 55 not found!"));
    }

    @Test
    void deleteUser() {

        UserDto userDto = new UserDto(1L, "Oleg", "oleg@yandex.ru");
        service.createUser(userDto);
        UserDto userDto1 = new UserDto(2L, "Roma", "roma@yandex.ru");
        service.createUser(userDto1);

        service.deleteUser(2L);

        assertThat(1, equalTo(service.findAllUsers().size()));
    }

    @Test
    void getAllUsers() {

        UserDto userDto = new UserDto(1L, "Oleg", "oleg@yandex.ru");
        service.createUser(userDto);
        UserDto userDto1 = new UserDto(2L, "Roma", "roma@yandex.ru");
        service.createUser(userDto1);
        UserDto userDto2 = new UserDto(3L, "Bob", "bob@yandex.ru");
        service.createUser(userDto2);

        List<UserDto> users = service.findAllUsers();

        System.out.println(users.get(0));
        System.out.println(users.get(1));
        System.out.println(users.get(2));

        assertThat(3, equalTo(users.size()));
    }
}
