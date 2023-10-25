package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAllUsers();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, int id);

    UserDto getUserById(int userId);

    void deleteUser(int userId);


}
