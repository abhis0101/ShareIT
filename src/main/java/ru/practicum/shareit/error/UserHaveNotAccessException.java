package ru.practicum.shareit.error;

public class UserHaveNotAccessException extends RuntimeException {

    public UserHaveNotAccessException(String message) {
        super(message);
    }
}
