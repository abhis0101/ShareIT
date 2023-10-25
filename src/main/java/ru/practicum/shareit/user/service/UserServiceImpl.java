package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.List;

import static ru.practicum.shareit.user.mapper.UserMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAllUsers() {
        log.info("List of all users received.");
        return getUserDtoList(userRepository.findAllUsers());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = toUser(userDto);
        checkUser(user);
        checkEmail(user);
        log.info("User saved.");
        return toUserDto(userRepository.createUser(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto, int id) {
        User userToUpdate = userRepository.getUserById(id);
        User user = toUser(userDto);

        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (!user.getEmail().equals(userToUpdate.getEmail())) {
                checkEmail(user);
            }
            userToUpdate.setEmail(user.getEmail());
        }
        log.info("User details have been updated.");
        return toUserDto(userRepository.updateUser(userToUpdate));
    }

    @Override
    public UserDto getUserById(int userId) {
        log.info("Received user with ID: " + userId);
        return toUserDto(userRepository.getUserById(userId));
    }

    @Override
    public void deleteUser(int userId) {
        userRepository.deleteUser(userId);
        log.info("User has been deleted.");
    }

    private void checkEmail(User user) {
        boolean emailExists = userRepository.findAllUsers().stream()
                .filter(u -> u.getId() != user.getId())
                .anyMatch(u -> u.getEmail().equals(user.getEmail()));
        if (emailExists) {
            throw new RuntimeException("A user with this email address already exists.");
        }
    }

    private void checkUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("The email cannot be empty and must contain the @ symbol.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("The username cannot be empty.");
        }
    }
}

