package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAllUsers() {
        log.info("Received a list of all users.");
        return userService.findAllUsers();
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        log.info("User saved.");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable(value = "id") Long id, @RequestBody UserDto userDto) {
        log.info("User data with id: " + id + "updated.");
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable(value = "id") Long id) {
        log.info("User with id: " + id + " deleted.");
        userService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable(value = "id") Long id) {
        log.info("Received user with ID: " + id);
        return userService.getUserById(id);
    }
}
