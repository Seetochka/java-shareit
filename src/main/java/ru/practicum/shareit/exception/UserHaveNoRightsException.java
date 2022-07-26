package ru.practicum.shareit.exception;

public class UserHaveNoRightsException extends LoggingException {
    public UserHaveNoRightsException(String message, String className) {
        super(message, className);
    }
}
