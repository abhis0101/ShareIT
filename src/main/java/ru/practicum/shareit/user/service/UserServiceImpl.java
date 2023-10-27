package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.error.EmailAlreadyExistException;
import ru.practicum.shareit.error.ModelNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static ru.practicum.shareit.user.mapper.UserMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        log.info("Received a list of all users.");
        return getUserDtoList(userRepository.findAll());
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        log.info("User saved.");
        return toUserDto(userRepository.save(toUser(userDto)));
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = getById(id);
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            checkEmailExistException(userDto.getEmail());
        }
        log.info("User details updated.");
        return toUserDto(userRepository.save(checksUser(user, userDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        User user = getById(userId);
        log.info("Received user with ID: " + userId);
        return toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.info("User deleted.");
    }

    private User checksUser(User user, UserDto userDto) {
        if (userDto.getName() != null && !userDto.getName().equals(user.getName())) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            user.setEmail(userDto.getEmail());
        }
        isValid(user);
        return user;
    }

    private void checkEmailExistException(String email) {
        if (userRepository.findAll().stream().anyMatch(a -> a.getEmail().equals(email)))
            throw new EmailAlreadyExistException("Email is already registered!");
    }

    private void isValid(User user) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid update data was passed!");
        }
    }

    private User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ModelNotFoundException(
                        String.format("User with id - %d not found!", userId)
                ));
    }

}

