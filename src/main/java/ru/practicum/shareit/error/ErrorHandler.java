package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({ModelNotFoundException.class, UserHaveNotAccessException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundExceptions(final RuntimeException e) {
        log.error("Model Not Found Exception");
        return new ErrorResponse("No data!", e.getMessage());
    }

    @ExceptionHandler({EmailAlreadyExistException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(final RuntimeException e) {
        log.error("Email Already Exists Exception");
        return new ErrorResponse("Data already exists!", e.getMessage());
    }

    @ExceptionHandler({InvalidBookingException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidBookingException(final InvalidBookingException e) {
        log.error("Invalid Booking Exception");
        return new ErrorResponse(e.getMessage(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidExceptions(final MethodArgumentNotValidException e) {
        log.error("Argument Not Valid Exception");
        return new ErrorResponse("Invalid object passed!", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("Type Mismatch Exception");
        return new ErrorResponse("Unknown state: " + Objects.requireNonNull(e.getValue()), e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception e) {
        log.error("Internal Server Error");
        return new ErrorResponse("Unknown error!", e.getMessage());
    }
}
