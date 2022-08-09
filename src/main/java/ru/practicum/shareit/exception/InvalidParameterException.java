package ru.practicum.shareit.exception;

/**
 * Исключение невалидного параметра
 */
public class InvalidParameterException extends LoggingException {
    public InvalidParameterException(String message, String className) {
        super(message, className);
    }
}
